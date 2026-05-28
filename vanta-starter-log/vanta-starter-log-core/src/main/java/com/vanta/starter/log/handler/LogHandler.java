package com.vanta.starter.log.handler;

import com.vanta.starter.log.enums.Include;
import com.vanta.starter.log.model.AccessLogContext;
import com.vanta.starter.log.model.LogRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Set;


/**
 * 日志生命周期处理端口。
 * <p>
 * AOP 和 MVC 拦截器都通过该接口完成操作日志、访问日志的开始、结束和注解字段解析。
 * 抽出该端口后，业务项目可以替换核心 Bean 来调整采集策略，而不需要改 starter 自动配置或切面代码。
 * </p>
 */
public interface LogHandler {

    /**
     * 判断目标方法是否需要采集操作日志。
     *
     * @param targetMethod 控制器或业务入口方法
     * @param targetClass  方法所属的目标类
     * @return {@code true} 表示需要采集；{@code false} 表示跳过
     */
    boolean isRecord(Method targetMethod, Class<?> targetClass);

    /**
     * 开始一次操作日志记录。
     *
     * @param startTime 请求或方法调用开始时间
     * @param request   当前 HTTP 请求
     * @return 操作日志开始态
     */
    LogRecord.Started start(Instant startTime, HttpServletRequest request);

    /**
     * 结束一次操作日志记录并生成基础日志模型。
     *
     * @param started  操作日志开始态
     * @param endTime  请求或方法调用结束时间
     * @param response 当前 HTTP 响应
     * @param includes 本次需要采集的字段集合
     * @return 操作日志记录
     */
    LogRecord finish(LogRecord.Started started, Instant endTime, HttpServletResponse response, Set<Include> includes);

    /**
     * 结束一次操作日志记录并补齐目标方法相关字段。
     *
     * @param started      操作日志开始态
     * @param endTime      请求或方法调用结束时间
     * @param response     当前 HTTP 响应
     * @param includes     全局默认采集字段集合
     * @param targetMethod 控制器或业务入口方法
     * @param targetClass  方法所属的目标类
     * @return 已补齐描述、模块等信息的操作日志记录
     */
    LogRecord finish(LogRecord.Started started,
                     Instant endTime,
                     HttpServletResponse response,
                     Set<Include> includes,
                     Method targetMethod,
                     Class<?> targetClass);

    /**
     * 根据目标方法解析并写入操作日志描述。
     *
     * @param logRecord    待补齐的操作日志记录
     * @param targetMethod 控制器或业务入口方法
     */
    void logDescription(LogRecord logRecord, Method targetMethod);

    /**
     * 根据目标类和目标方法解析并写入所属模块。
     *
     * @param logRecord    待补齐的操作日志记录
     * @param targetMethod 控制器或业务入口方法
     * @param targetClass  方法所属的目标类
     */
    void logModule(LogRecord logRecord, Method targetMethod, Class<?> targetClass);

    /**
     * 合并全局采集字段和注解级字段覆写。
     *
     * @param includes     全局默认采集字段集合
     * @param targetMethod 控制器或业务入口方法
     * @param targetClass  方法所属的目标类
     * @return 本次操作日志最终需要采集的字段集合
     */
    Set<Include> getIncludes(Set<Include> includes, Method targetMethod, Class<?> targetClass);

    /**
     * 开始一次访问日志记录。
     *
     * @param accessLogContext 包含请求、开始时间和配置的访问日志上下文
     * @since 2.10.0
     */
    void accessLogStart(AccessLogContext accessLogContext);

    /**
     * 结束一次访问日志记录。
     *
     * @param accessLogContext 包含响应和结束时间的访问日志上下文
     * @since 2.10.0
     */
    void accessLogFinish(AccessLogContext accessLogContext);
}
