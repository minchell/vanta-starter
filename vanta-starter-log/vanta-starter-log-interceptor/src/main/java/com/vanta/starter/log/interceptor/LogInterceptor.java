package com.vanta.starter.log.interceptor;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.vanta.starter.log.dao.LogDao;
import com.vanta.starter.log.handler.LogHandler;
import com.vanta.starter.log.http.servlet.RecordableServletHttpRequest;
import com.vanta.starter.log.http.servlet.RecordableServletHttpResponse;
import com.vanta.starter.log.model.AccessLogContext;
import com.vanta.starter.log.model.LogProperties;
import com.vanta.starter.log.model.LogRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.time.Instant;


/**
 * 基于 Spring MVC {@link HandlerInterceptor} 的日志拦截器入口。
 * <p>
 * 该拦截器适合没有启用 AOP 日志切面、但仍希望在标准 MVC 请求生命周期内采集访问日志和操作日志的项目。
 * 它只依赖 Servlet 请求响应、日志配置、日志处理器和日志持久化接口，不绑定 具体业务项目的任何业务对象，
 * 因此可以作为独立 starter 被其他 Spring Boot Web 项目复用。
 * </p>
 */
public class LogInterceptor implements HandlerInterceptor {

    /**
     * 当前拦截器使用的日志门面。
     * <p>
     * 仅用于记录日志采集自身的异常，不能替代业务系统的审计日志，也不能写入敏感请求体。
     * </p>
     */
    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);
    /**
     * 日志 starter 的配置属性。
     * <p>
     * 访问日志开关、排除路径、采集字段等策略都从这里读取，业务方可以通过配置文件调整默认行为。
     * </p>
     */
    private final LogProperties logProperties;
    /**
     * 日志生命周期处理器。
     * <p>
     * 负责把 Servlet 请求响应转换为统一日志模型，并根据注解、OpenAPI 元数据和配置决定最终采集内容。
     * </p>
     */
    private final LogHandler logHandler;
    /**
     * 操作日志写入接口。
     * <p>
     * 默认实现只输出日志，业务方可以注册自己的 {@link LogDao} Bean 将日志写入数据库、消息队列或审计平台。
     * </p>
     */
    private final LogDao logDao;
    /**
     * 当前请求的操作日志开始态。
     * <p>
     * 使用 TTL 是为了兼容异步线程传递场景；请求结束后必须在 {@link #afterCompletion} 中清理，避免线程复用导致串日志。
     * </p>
     */
    private final TransmittableThreadLocal<LogRecord.Started> logTtl = new TransmittableThreadLocal<>();

    /**
     * 创建 MVC 日志拦截器。
     *
     * @param logProperties 日志采集配置，不能为 {@code null}
     * @param logHandler    日志生命周期处理器，不能为 {@code null}
     * @param logDao        操作日志写入接口，不能为 {@code null}
     */
    public LogInterceptor(LogProperties logProperties, LogHandler logHandler, LogDao logDao) {
        this.logProperties = logProperties;
        this.logHandler = logHandler;
        this.logDao = logDao;
    }

    /**
     * 在控制器方法执行前启动访问日志和操作日志采集。
     * <p>
     * 访问日志面向所有可进入拦截器的请求；操作日志只针对 {@link HandlerMethod} 且未被隐藏或忽略的控制器方法。
     * </p>
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  Spring MVC 解析出的处理器对象
     * @return 固定返回 {@code true}，日志 starter 不拦截业务请求执行
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Instant startTime = Instant.now();
        // 访问日志
        logHandler.accessLogStart(
                AccessLogContext.builder()
                        .startTime(startTime)
                        .request(new RecordableServletHttpRequest(request))
                        .properties(logProperties)
                        .build()
        );
        // 开始日志记录
        if (this.isRecord(handler)) {
            LogRecord.Started startedLogRecord = logHandler.start(startTime, request);
            logTtl.set(startedLogRecord);
        }
        return true;
    }

    /**
     * 在请求完成后结束访问日志和操作日志采集。
     * <p>
     * 该方法会把响应状态、耗时、注解描述、模块信息等补齐后交给 {@link LogDao}。
     * 不管日志写入是否成功，最终都会清理当前线程持有的日志上下文。
     * </p>
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  Spring MVC 解析出的处理器对象
     * @param e        MVC 请求处理链抛出的异常，正常完成时为 {@code null}
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception e) {
        try {
            Instant endTime = Instant.now();
            // 访问日志
            logHandler.accessLogFinish(
                    AccessLogContext.builder()
                            .endTime(endTime)
                            .response(new RecordableServletHttpResponse(response))
                            .build()
            );
            LogRecord.Started startedLogRecord = logTtl.get();
            if (startedLogRecord == null) {
                return;
            }
            // 结束日志记录
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method targetMethod = handlerMethod.getMethod();
            Class<?> targetClass = handlerMethod.getBeanType();
            LogRecord logRecord = logHandler.finish(
                    startedLogRecord, endTime, response, logProperties.getIncludes(), targetMethod, targetClass);
            logDao.add(logRecord);
        } catch (Exception ex) {
            log.error("Logging http log occurred an error: {}.", ex.getMessage(), ex);
            throw ex;
        } finally {
            logTtl.remove();
        }
    }

    /**
     * 判断当前处理器是否需要采集操作日志。
     *
     * @param handler Spring MVC 处理器对象
     * @return {@code true} 表示需要采集操作日志；{@code false} 表示跳过操作日志，仅保留访问日志
     */
    private boolean isRecord(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return false;
        }
        return logHandler.isRecord(handlerMethod.getMethod(), handlerMethod.getBeanType());
    }
}
