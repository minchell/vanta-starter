package com.vanta.starter.log.handler;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.vanta.starter.log.annotation.Log;
import com.vanta.starter.log.dao.LogDao;
import com.vanta.starter.log.enums.Include;
import com.vanta.starter.log.http.RecordableHttpRequest;
import com.vanta.starter.log.http.RecordableHttpResponse;
import com.vanta.starter.log.http.servlet.RecordableServletHttpRequest;
import com.vanta.starter.log.http.servlet.RecordableServletHttpResponse;
import com.vanta.starter.log.model.AccessLogContext;
import com.vanta.starter.log.model.AccessLogProperties;
import com.vanta.starter.log.model.LogRecord;
import com.vanta.starter.log.util.AccessLogUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


/**
 * 日志生命周期处理器基类。
 * <p>
 * 该基类集中实现操作日志和访问日志的通用组装逻辑：判断是否采集、创建开始态、结束记录、解析注解字段、
 * 输出访问日志。具体项目如果需要改变持久化或输出行为，可以优先替换 {@link LogDao}，也可以继承本类微调规则。
 * </p>
 */
public abstract class AbstractLogHandler implements LogHandler {

    /**
     * 访问日志输出门面。
     * <p>
     * 默认只写本地应用日志，避免 starter 被引入后自动连接外部审计平台或日志服务。
     * </p>
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractLogHandler.class);

    // ExecutorService executor = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(10));
    // TtlRunnable.get(() -> logDao.insert(ctx))
    /**
     * 当前请求的访问日志上下文。
     * <p>
     * 访问日志开始和结束通常处于同一个请求链路，使用 TTL 可以在业务引入异步执行时尽量保持上下文可传递；
     * 结束日志输出后必须移除，避免线程池复用造成请求信息泄漏。
     * </p>
     */
    private final TransmittableThreadLocal<AccessLogContext> logContextThread = new TransmittableThreadLocal<>();

    /**
     * 判断目标接口是否需要采集操作日志。
     * <p>
     * 默认会尊重 OpenAPI 的隐藏标记、Swagger 的 {@link Hidden} 注解，以及日志 starter 自己的 {@link Log#ignore()} 配置。
     * </p>
     *
     * @param targetMethod 控制器或业务入口方法
     * @param targetClass  方法所属的目标类
     * @return {@code true} 表示需要采集；{@code false} 表示跳过
     */
    @Override
    public boolean isRecord(Method targetMethod, Class<?> targetClass) {
        // 如果接口被隐藏，不记录日志
        Operation methodOperation = AnnotationUtil.getAnnotation(targetMethod, Operation.class);
        if (methodOperation != null && methodOperation.hidden()) {
            return false;
        }
        Hidden methodHidden = AnnotationUtil.getAnnotation(targetMethod, Hidden.class);
        if (methodHidden != null) {
            return false;
        }
        if (targetClass.getDeclaredAnnotation(Hidden.class) != null) {
            return false;
        }
        // 如果接口方法或类上有 @Log 注解，且要求忽略该接口，则不记录日志
        Log methodLog = AnnotationUtil.getAnnotation(targetMethod, Log.class);
        if (methodLog != null && methodLog.ignore()) {
            return false;
        }
        Log classLog = AnnotationUtil.getAnnotation(targetClass, Log.class);
        return classLog == null || !classLog.ignore();
    }

    /**
     * 创建一次操作日志的开始态。
     *
     * @param startTime 请求或方法调用开始时间
     * @param request   当前 Servlet 请求
     * @return 操作日志开始态，后续结束阶段会基于它补齐耗时和响应信息
     */
    @Override
    public LogRecord.Started start(Instant startTime, HttpServletRequest request) {
        return LogRecord.start(startTime, new RecordableServletHttpRequest(request));
    }

    /**
     * 结束一次操作日志并补齐注解相关字段。
     * <p>
     * 该重载会合并全局采集字段和 {@link Log} 注解上的 include/exclude 配置，再按需解析描述和模块。
     * </p>
     *
     * @param started      操作日志开始态
     * @param endTime      请求或方法调用结束时间
     * @param response     当前 Servlet 响应
     * @param includes     全局默认采集字段
     * @param targetMethod 控制器或业务入口方法
     * @param targetClass  方法所属的目标类
     * @return 已补齐响应、耗时、描述和模块信息的操作日志记录
     */
    @Override
    public LogRecord finish(LogRecord.Started started,
                            Instant endTime,
                            HttpServletResponse response,
                            Set<Include> includes,
                            Method targetMethod,
                            Class<?> targetClass) {
        Set<Include> includeSet = this.getIncludes(includes, targetMethod, targetClass);
        LogRecord logRecord = this.finish(started, endTime, response, includeSet);
        // 记录日志描述
        if (includeSet.contains(Include.DESCRIPTION)) {
            this.logDescription(logRecord, targetMethod);
        }
        // 记录所属模块
        if (includeSet.contains(Include.MODULE)) {
            this.logModule(logRecord, targetMethod, targetClass);
        }
        return logRecord;
    }

    /**
     * 结束一次操作日志并生成基础日志记录。
     *
     * @param started  操作日志开始态
     * @param endTime  请求或方法调用结束时间
     * @param response 当前 Servlet 响应
     * @param includes 本次实际采集字段集合
     * @return 已补齐响应和耗时信息的操作日志记录
     */
    @Override
    public LogRecord finish(LogRecord.Started started,
                            Instant endTime,
                            HttpServletResponse response,
                            Set<Include> includes) {
        return started.finish(endTime, new RecordableServletHttpResponse(response), includes);
    }

    /**
     * 解析并写入操作日志描述。
     * <p>
     * 优先读取 {@link Log#value()}，没有显式配置时退回到 OpenAPI {@link Operation#summary()}。
     * </p>
     *
     * @param logRecord    待补齐的日志记录
     * @param targetMethod 控制器或业务入口方法
     */
    @Override
    public void logDescription(LogRecord logRecord, Method targetMethod) {
        logRecord.setDescription("请在该接口方法上添加 @com.vanta.starter.log.annotation.Log(value) 来指定日志描述");
        Log methodLog = AnnotationUtil.getAnnotation(targetMethod, Log.class);
        // 例如：@Log("新增部门") -> 新增部门
        if (methodLog != null && CharSequenceUtil.isNotBlank(methodLog.value())) {
            logRecord.setDescription(methodLog.value());
            return;
        }
        // 例如：@Operation(summary="新增部门") -> 新增部门
        Operation methodOperation = AnnotationUtil.getAnnotation(targetMethod, Operation.class);
        if (methodOperation != null && CharSequenceUtil.isNotBlank(methodOperation.summary())) {
            logRecord.setDescription(methodOperation.summary());
        }
    }

    /**
     * 解析并写入操作日志所属模块。
     * <p>
     * 优先级依次为方法级 {@link Log#module()}、类级 {@link Log#module()}、OpenAPI {@link Tag#name()}。
     * </p>
     *
     * @param logRecord    待补齐的日志记录
     * @param targetMethod 控制器或业务入口方法
     * @param targetClass  方法所属的目标类
     */
    @Override
    public void logModule(LogRecord logRecord, Method targetMethod, Class<?> targetClass) {
        logRecord.setModule("请在该接口方法或类上添加 @com.vanta.starter.log.annotation.Log(module) 来指定所属模块");
        Log methodLog = AnnotationUtil.getAnnotation(targetMethod, Log.class);
        // 例如：@Log(module = "部门管理") -> 部门管理
        // 方法级注解优先级高于类级注解
        if (methodLog != null && CharSequenceUtil.isNotBlank(methodLog.module())) {
            logRecord.setModule(methodLog.module());
            return;
        }
        Log classLog = AnnotationUtil.getAnnotation(targetClass, Log.class);
        if (classLog != null && CharSequenceUtil.isNotBlank(classLog.module())) {
            logRecord.setModule(classLog.module());
            return;
        }
        // 例如：@Tag(name = "部门管理") -> 部门管理
        Tag classTag = AnnotationUtil.getAnnotation(targetClass, Tag.class);
        if (classTag != null && CharSequenceUtil.isNotBlank(classTag.name())) {
            logRecord.setModule(classTag.name());
        }
    }

    /**
     * 合并全局采集字段和注解级字段覆写。
     * <p>
     * 类级注解先参与合并，方法级注解后参与合并，因此方法级配置可以覆盖类级默认策略。
     * </p>
     *
     * @param includes     全局默认采集字段
     * @param targetMethod 控制器或业务入口方法
     * @param targetClass  方法所属的目标类
     * @return 本次操作日志最终需要采集的字段集合
     */
    @Override
    public Set<Include> getIncludes(Set<Include> includes, Method targetMethod, Class<?> targetClass) {
        Log classLog = AnnotationUtil.getAnnotation(targetClass, Log.class);
        Set<Include> includeSet = new HashSet<>(includes);
        if (classLog != null) {
            this.processInclude(includeSet, classLog);
        }
        // 方法级注解优先级高于类级注解
        Log methodLog = AnnotationUtil.getAnnotation(targetMethod, Log.class);
        if (methodLog != null) {
            this.processInclude(includeSet, methodLog);
        }
        return includeSet;
    }

    /**
     * 将单个 {@link Log} 注解上的 include/exclude 配置应用到采集字段集合。
     *
     * @param includes      当前已经合并出的采集字段集合
     * @param logAnnotation 需要应用的日志注解
     */
    private void processInclude(Set<Include> includes, Log logAnnotation) {
        Include[] includeArr = logAnnotation.includes();
        if (includeArr.length > 0) {
            includes.addAll(Set.of(includeArr));
        }
        Include[] excludeArr = logAnnotation.excludes();
        if (excludeArr.length > 0) {
            includes.removeAll(Set.of(excludeArr));
        }
    }

    /**
     * 开始记录一次访问日志。
     * <p>
     * 当访问日志未开启或请求路径命中排除规则时直接返回；默认行为只写本地日志，不产生远程副作用。
     * </p>
     *
     * @param context 当前访问日志上下文，必须包含请求、开始时间和日志配置
     */
    @Override
    public void accessLogStart(AccessLogContext context) {
        AccessLogProperties properties = context.getProperties().getAccessLog();
        // 是否需要打印
        if (!properties.isEnabled() || AccessLogUtils.exclusionPath(context.getProperties(), context.getRequest().getPath())) {
            return;
        }
        // 构建上下文
        logContextThread.set(context);
        RecordableHttpRequest request = context.getRequest();
        String param = AccessLogUtils.getParam(request, properties);
        log.info(param != null ? "[Start] [{}] {} param: {}" : "[Start] [{}] {}", request.getMethod(), request.getPath(), param);
    }

    /**
     * 结束记录一次访问日志。
     * <p>
     * 结束阶段会读取开始阶段保存的请求上下文，输出 HTTP 方法、路径、响应状态和耗时，并最终清理 TTL。
     * </p>
     *
     * @param context 当前访问日志结束上下文，必须包含响应和结束时间
     */
    @Override
    public void accessLogFinish(AccessLogContext context) {
        AccessLogContext logContext = logContextThread.get();
        if (ObjectUtil.isEmpty(logContext)) {
            return;
        }
        try {
            RecordableHttpRequest request = logContext.getRequest();
            RecordableHttpResponse response = context.getResponse();
            Duration timeTaken = Duration.between(logContext.getStartTime(), context.getEndTime());
            log.info("[End] [{}] {} {} {}ms", request.getMethod(), request.getPath(), response.getStatus(), timeTaken.toMillis());
        } finally {
            logContextThread.remove();
        }
    }
}
