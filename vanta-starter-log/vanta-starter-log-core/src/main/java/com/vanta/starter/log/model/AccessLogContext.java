package com.vanta.starter.log.model;

import com.vanta.starter.log.http.RecordableHttpRequest;
import com.vanta.starter.log.http.RecordableHttpResponse;

import java.time.Instant;


/**
 * 访问日志上下文。
 * <p>
 * 访问日志分为开始和结束两个阶段：开始阶段保存请求、开始时间和配置，结束阶段补充响应和结束时间。
 * 该上下文通过 Builder 传递，避免多个松散参数在切面、拦截器和处理器之间扩散。
 * </p>
 */
public class AccessLogContext {

    /**
     * 请求开始时间。
     */
    private final Instant startTime;
    /**
     * 可记录的请求信息。
     */
    private final RecordableHttpRequest request;
    /**
     * 可记录的响应信息。
     */
    private final RecordableHttpResponse response;
    /**
     * 日志 starter 配置。
     */
    private final LogProperties properties;
    /**
     * 请求结束时间。
     */
    private Instant endTime;

    private AccessLogContext(Builder builder) {
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.request = builder.request;
        this.response = builder.response;
        this.properties = builder.properties;
    }

    /**
     * 创建访问日志上下文构建器。
     *
     * @return 新的访问日志上下文构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 读取请求开始时间。
     *
     * @return 请求开始时间
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * 读取请求结束时间。
     *
     * @return 请求结束时间
     */
    public Instant getEndTime() {
        return endTime;
    }

    /**
     * 设置请求结束时间。
     *
     * @param endTime 请求结束时间
     */
    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    /**
     * 读取可记录的请求信息。
     *
     * @return 可记录的请求信息
     */
    public RecordableHttpRequest getRequest() {
        return request;
    }

    /**
     * 读取可记录的响应信息。
     *
     * @return 可记录的响应信息
     */
    public RecordableHttpResponse getResponse() {
        return response;
    }

    /**
     * 读取日志 starter 配置。
     *
     * @return 日志 starter 配置
     */
    public LogProperties getProperties() {
        return properties;
    }

    /**
     * 访问日志上下文构建器。
     * <p>
     * Builder 只做字段收集，不做远程调用或复杂校验，保持访问日志链路轻量。
     * </p>
     */
    public static class Builder {

        /**
         * 请求开始时间。
         */
        private Instant startTime;
        /**
         * 请求结束时间。
         */
        private Instant endTime;
        /**
         * 可记录的请求信息。
         */
        private RecordableHttpRequest request;
        /**
         * 可记录的响应信息。
         */
        private RecordableHttpResponse response;
        /**
         * 日志 starter 配置。
         */
        private LogProperties properties;

        private Builder() {
        }

        /**
         * 设置请求开始时间。
         *
         * @param startTime 请求开始时间
         * @return 当前构建器
         */
        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        /**
         * 设置请求结束时间。
         *
         * @param endTime 请求结束时间
         * @return 当前构建器
         */
        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * 设置可记录的请求信息。
         *
         * @param request 可记录的请求信息
         * @return 当前构建器
         */
        public Builder request(RecordableHttpRequest request) {
            this.request = request;
            return this;
        }

        /**
         * 设置可记录的响应信息。
         *
         * @param response 可记录的响应信息
         * @return 当前构建器
         */
        public Builder response(RecordableHttpResponse response) {
            this.response = response;
            return this;
        }

        /**
         * 设置日志 starter 配置。
         *
         * @param properties 日志 starter 配置
         * @return 当前构建器
         */
        public Builder properties(LogProperties properties) {
            this.properties = properties;
            return this;
        }

        /**
         * 构建访问日志上下文。
         *
         * @return 访问日志上下文
         */
        public AccessLogContext build() {
            return new AccessLogContext(this);
        }
    }
}
