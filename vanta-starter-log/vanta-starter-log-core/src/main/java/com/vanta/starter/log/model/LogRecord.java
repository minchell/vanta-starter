package com.vanta.starter.log.model;

import com.vanta.starter.log.enums.Include;
import com.vanta.starter.log.http.RecordableHttpRequest;
import com.vanta.starter.log.http.RecordableHttpResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;


/**
 * 一条完整的操作日志记录。
 * <p>
 * 日志记录由开始时间、请求快照、响应快照、耗时、业务描述、所属模块和异常摘要组成。
 * 该模型只表达日志事实，不负责持久化；持久化由 {@code LogDao} 决定。
 * </p>
 */
public class LogRecord {

    /**
     * 操作开始时间。
     * <p>用于排序、计算耗时和审计追踪。</p>
     */
    private final Instant timestamp;
    /**
     * 操作描述。
     * <p>通常来自 {@code @Log(value)} 或 OpenAPI {@code @Operation(summary)}。</p>
     */
    private String description;
    /**
     * 操作所属模块。
     * <p>通常来自 {@code @Log(module)} 或 OpenAPI {@code @Tag(name)}。</p>
     */
    private String module;
    /**
     * 请求信息快照。
     */
    private LogRequest request;
    /**
     * 响应信息快照。
     */
    private LogResponse response;
    /**
     * 操作耗时。
     * <p>由开始时间和结束时间计算得到。</p>
     */
    private Duration timeTaken;
    /**
     * 异常摘要。
     * <p>目标方法抛出异常时保存截断后的错误信息，避免日志字段无限增长。</p>
     */
    private String errorMsg;

    /**
     * 创建完整操作日志记录。
     *
     * @param timestamp 操作开始时间
     * @param request   请求信息快照
     * @param response  响应信息快照
     * @param timeTaken 操作耗时
     */
    public LogRecord(Instant timestamp, LogRequest request, LogResponse response, Duration timeTaken) {
        this.timestamp = timestamp;
        this.request = request;
        this.response = response;
        this.timeTaken = timeTaken;
    }

    /**
     * 使用当前时间开始一条操作日志。
     *
     * @param request 可被日志系统读取的请求适配器
     * @return 操作日志开始态
     */
    public static Started start(RecordableHttpRequest request) {
        return start(Instant.now(), request);
    }

    /**
     * 使用指定时间开始一条操作日志。
     *
     * @param timestamp 操作开始时间
     * @param request   可被日志系统读取的请求适配器
     * @return 操作日志开始态
     */
    public static Started start(Instant timestamp, RecordableHttpRequest request) {
        return new Started(timestamp, request);
    }

    /**
     * 读取操作描述。
     *
     * @return 操作描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置操作描述。
     *
     * @param description 操作描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 读取操作所属模块。
     *
     * @return 操作所属模块
     */
    public String getModule() {
        return module;
    }

    /**
     * 设置操作所属模块。
     *
     * @param module 操作所属模块
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * 读取请求信息快照。
     *
     * @return 请求信息快照
     */
    public LogRequest getRequest() {
        return request;
    }

    /**
     * 设置请求信息快照。
     *
     * @param request 请求信息快照
     */
    public void setRequest(LogRequest request) {
        this.request = request;
    }

    /**
     * 读取响应信息快照。
     *
     * @return 响应信息快照
     */
    public LogResponse getResponse() {
        return response;
    }

    /**
     * 设置响应信息快照。
     *
     * @param response 响应信息快照
     */
    public void setResponse(LogResponse response) {
        this.response = response;
    }

    /**
     * 读取操作耗时。
     *
     * @return 操作耗时
     */
    public Duration getTimeTaken() {
        return timeTaken;
    }

    /**
     * 设置操作耗时。
     *
     * @param timeTaken 操作耗时
     */
    public void setTimeTaken(Duration timeTaken) {
        this.timeTaken = timeTaken;
    }

    /**
     * 读取操作开始时间。
     *
     * @return 操作开始时间
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * 读取异常摘要。
     *
     * @return 异常摘要，成功请求通常为 {@code null}
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 设置异常摘要。
     *
     * @param errorMsg 异常摘要
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 操作日志开始态。
     * <p>
     * 开始态只保存请求和开始时间，等目标方法执行结束后再调用 {@link #finish} 生成完整日志记录。
     * </p>
     */
    public static final class Started {

        /**
         * 操作开始时间。
         */
        private final Instant timestamp;

        /**
         * 可被日志系统读取的请求适配器。
         * <p>结束阶段会根据采集字段集合从这里生成 {@link LogRequest}。</p>
         */
        private final RecordableHttpRequest request;

        private Started(Instant timestamp, RecordableHttpRequest request) {
            this.timestamp = timestamp;
            this.request = request;
        }

        /**
         * 结束操作日志并生成完整记录。
         *
         * @param timestamp 操作结束时间
         * @param response  可被日志系统读取的响应适配器
         * @param includes  本次操作日志需要采集的字段集合
         * @return 完整操作日志记录
         */
        public LogRecord finish(Instant timestamp, RecordableHttpResponse response, Set<Include> includes) {
            LogRequest logRequest = new LogRequest(this.request, includes);
            LogResponse logResponse = new LogResponse(response, includes);
            Duration duration = Duration.between(this.timestamp, timestamp);
            return new LogRecord(this.timestamp, logRequest, logResponse, duration);
        }
    }
}
