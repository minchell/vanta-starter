package com.vanta.starter.log.aspect;

import cn.hutool.core.text.CharSequenceUtil;
import com.vanta.starter.log.annotation.Log;
import com.vanta.starter.log.dao.LogDao;
import com.vanta.starter.log.handler.LogHandler;
import com.vanta.starter.log.model.LogProperties;
import com.vanta.starter.log.model.LogRecord;
import com.vanta.starter.web.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Instant;

/**
 * 基于 AOP 的操作日志切面。
 * <p>
 * 该切面只处理显式标注 {@link Log} 的方法，适合希望用注解精确声明“哪些业务动作需要审计”的项目。
 * 切面本身不持久化具体业务数据，只把日志记录交给 {@link LogDao}，业务方可以替换该接口实现自己的落库策略。
 * </p>
 */
@Aspect
public class LogAspect {

    /**
     * 当前切面使用的日志门面。
     * <p>
     * 仅记录日志采集失败这类 starter 内部异常，避免与业务审计日志混在一起。
     * </p>
     */
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    /**
     * 日志 starter 的配置属性。
     * <p>
     * 用于读取 URI 排除规则和默认采集字段，使注解能力可以被配置文件统一约束。
     * </p>
     */
    private final LogProperties logProperties;
    /**
     * 日志生命周期处理器。
     * <p>
     * 负责创建开始态、结束态，并从注解和 OpenAPI 元数据中补齐描述、模块等审计字段。
     * </p>
     */
    private final LogHandler logHandler;
    /**
     * 操作日志写入接口。
     * <p>
     * 默认实现保持轻量输出；业务系统需要远程写入时，应通过替换 Bean 的方式显式接入。
     * </p>
     */
    private final LogDao logDao;

    /**
     * 创建操作日志切面。
     *
     * @param logProperties 日志采集配置，不能为 {@code null}
     * @param logHandler    日志生命周期处理器，不能为 {@code null}
     * @param logDao        操作日志写入接口，不能为 {@code null}
     */
    public LogAspect(LogProperties logProperties, LogHandler logHandler, LogDao logDao) {
        this.logProperties = logProperties;
        this.logHandler = logHandler;
        this.logDao = logDao;
    }

    /**
     * 匹配所有标注 {@link Log} 注解的方法。
     * <p>
     * 仅以注解作为入口，可以避免切面默认扫描所有控制器造成额外开销。
     * </p>
     */
    @Pointcut("@annotation(com.vanta.starter.log.annotation.Log)")
    public void pointcut() {
    }

    /**
     * 环绕执行目标方法并记录一次操作日志。
     * <p>
     * 切面在目标方法执行前创建日志开始态，在 finally 阶段补充响应、耗时和异常摘要，
     * 因此即使业务方法抛出异常也能保留失败审计信息。
     * </p>
     *
     * @param joinPoint 当前 AOP 连接点
     * @return 目标方法的原始返回值
     * @throws Throwable 透传目标方法或日志处理过程中的异常
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        // 指定规则不记录
        HttpServletRequest request = ServletUtils.getRequest();
        Method targetMethod = this.getMethod(joinPoint);
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (!isRecord(targetMethod, targetClass)) {
            return joinPoint.proceed();
        }
        String errorMsg = null;
        // 开始记录
        LogRecord.Started startedLogRecord = logHandler.start(startTime, request);
        try {
            // 执行目标方法
            return joinPoint.proceed();
        } catch (Exception e) {
            errorMsg = CharSequenceUtil.sub(e.getMessage(), 0, 2000);
            throw e;
        } finally {
            try {
                Instant endTime = Instant.now();
                HttpServletResponse response = ServletUtils.getResponse();
                LogRecord logRecord = logHandler.finish(
                        startedLogRecord, endTime, response, logProperties.getIncludes(), targetMethod, targetClass);
                // 记录异常信息
                if (errorMsg != null) {
                    logRecord.setErrorMsg(errorMsg);
                }
                logDao.add(logRecord);
            } catch (Exception e) {
                log.error("Logging http log occurred an error: {}.", e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * 判断当前方法是否需要记录操作日志。
     * <p>
     * 非 Web 请求、无响应上下文、命中排除 URI 或被 {@link LogHandler} 判定为忽略时都会跳过采集。
     * </p>
     *
     * @param targetMethod 被代理的目标方法
     * @param targetClass  被代理的目标类
     * @return {@code true} 表示需要记录；{@code false} 表示跳过记录
     */
    private boolean isRecord(Method targetMethod, Class<?> targetClass) {
        // 非 Web 环境不记录
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || attributes.getResponse() == null) {
            return false;
        }
        // 如果接口匹配排除列表，不记录日志
        if (logProperties.isMatchExcludeUri(attributes.getRequest().getRequestURI())) {
            return false;
        }
        return logHandler.isRecord(targetMethod, targetClass);
    }

    /**
     * 从 AOP 连接点解析目标方法。
     *
     * @param joinPoint 当前 AOP 连接点
     * @return 被代理的 Java 方法
     */
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
