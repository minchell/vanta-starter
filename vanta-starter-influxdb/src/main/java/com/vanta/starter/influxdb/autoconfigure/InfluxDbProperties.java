package com.vanta.starter.influxdb.autoconfigure;

import com.influxdb.LogLevel;
import com.influxdb.client.WriteOptions;
import com.vanta.starter.core.constant.PropertiesConstants;
import io.reactivex.rxjava3.core.BackpressureOverflowStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Vanta InfluxDB 配置。
 *
 * <p>该配置兼容 InfluxDB 1.x 和 2.x。1.x 使用 database/retentionPolicy，
 * 2.x 使用 token/org/bucket。生产环境必须显式配置 url 和认证信息。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.INFLUXDB)
public class InfluxDbProperties {

    /**
     * 是否启用 InfluxDB 自动配置。
     */
    private boolean enabled = false;

    /**
     * InfluxDB 服务地址，例如 http://127.0.0.1:8086。
     */
    private String url;

    /**
     * InfluxDB 1.x 或基础认证用户名。
     */
    private String username;

    /**
     * InfluxDB 1.x 或基础认证密码。
     */
    private String password;

    /**
     * InfluxDB 2.x token。
     */
    private String token;

    /**
     * InfluxDB 2.x organization。
     */
    private String org;

    /**
     * InfluxDB 2.x bucket。
     */
    private String bucket;

    /**
     * InfluxDB 1.x database。
     */
    private String database;

    /**
     * InfluxDB 1.x retention policy。
     */
    private String retentionPolicy = "autogen";

    /**
     * InfluxDB client HTTP 日志级别。
     */
    private LogLevel logLevel = LogLevel.NONE;

    /**
     * readTimeout 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的超时或间隔配置，用于控制调用等待、重试或批处理节奏。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Duration readTimeout = Duration.ofSeconds(10);

    /**
     * writeTimeout 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的超时或间隔配置，用于控制调用等待、重试或批处理节奏。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Duration writeTimeout = Duration.ofSeconds(10);

    /**
     * connectTimeout 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的超时或间隔配置，用于控制调用等待、重试或批处理节奏。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Duration connectTimeout = Duration.ofSeconds(10);

    /**
     * batchSize 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer batchSize = WriteOptions.DEFAULT_BATCH_SIZE;

    /**
     * flushInterval 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的超时或间隔配置，用于控制调用等待、重试或批处理节奏。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer flushInterval = WriteOptions.DEFAULT_FLUSH_INTERVAL;

    /**
     * jitterInterval 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的超时或间隔配置，用于控制调用等待、重试或批处理节奏。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer jitterInterval = WriteOptions.DEFAULT_JITTER_INTERVAL;

    /**
     * retryInterval 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的超时或间隔配置，用于控制调用等待、重试或批处理节奏。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer retryInterval = WriteOptions.DEFAULT_RETRY_INTERVAL;

    /**
     * maxRetries 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer maxRetries = WriteOptions.DEFAULT_MAX_RETRIES;

    /**
     * maxRetryDelay 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer maxRetryDelay = WriteOptions.DEFAULT_MAX_RETRY_DELAY;

    /**
     * maxRetryTime 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer maxRetryTime = WriteOptions.DEFAULT_MAX_RETRY_TIME;

    /**
     * exponentialBase 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer exponentialBase = WriteOptions.DEFAULT_EXPONENTIAL_BASE;

    /**
     * bufferLimit 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private Integer bufferLimit = WriteOptions.DEFAULT_BUFFER_LIMIT;

    /**
     * backpressureStrategy 字段。
     * <p>用于保存 InfluxDB 时序数据能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private BackpressureOverflowStrategy backpressureStrategy = BackpressureOverflowStrategy.DROP_OLDEST;

    /**
     * 获取是否启用 InfluxDB 自动配置。
     *
     * @return 是否启用 InfluxDB 自动配置
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 InfluxDB 自动配置。
     *
     * @param enabled 是否启用 InfluxDB 自动配置
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取 InfluxDB 服务地址，例如 http://127.0.0.1:8086。
     *
     * @return InfluxDB 服务地址，例如 http://127.0.0.1:8086
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置 InfluxDB 服务地址，例如 http://127.0.0.1:8086。
     *
     * @param url InfluxDB 服务地址，例如 http://127.0.0.1:8086
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取 InfluxDB 1.x 或基础认证用户名。
     *
     * @return InfluxDB 1.x 或基础认证用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置 InfluxDB 1.x 或基础认证用户名。
     *
     * @param username InfluxDB 1.x 或基础认证用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取 InfluxDB 1.x 或基础认证密码。
     *
     * @return InfluxDB 1.x 或基础认证密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置 InfluxDB 1.x 或基础认证密码。
     *
     * @param password InfluxDB 1.x 或基础认证密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取 InfluxDB 2.x token。
     *
     * @return InfluxDB 2.x token
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置 InfluxDB 2.x token。
     *
     * @param token InfluxDB 2.x token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取 InfluxDB 2.x organization。
     *
     * @return InfluxDB 2.x organization
     */
    public String getOrg() {
        return org;
    }

    /**
     * 设置 InfluxDB 2.x organization。
     *
     * @param org InfluxDB 2.x organization
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * 获取 InfluxDB 2.x bucket。
     *
     * @return InfluxDB 2.x bucket
     */
    public String getBucket() {
        return bucket;
    }

    /**
     * 设置 InfluxDB 2.x bucket。
     *
     * @param bucket InfluxDB 2.x bucket
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * 获取 InfluxDB 1.x database。
     *
     * @return InfluxDB 1.x database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * 设置 InfluxDB 1.x database。
     *
     * @param database InfluxDB 1.x database
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * 获取 InfluxDB 1.x retention policy。
     *
     * @return InfluxDB 1.x retention policy
     */
    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    /**
     * 设置 InfluxDB 1.x retention policy。
     *
     * @param retentionPolicy InfluxDB 1.x retention policy
     */
    public void setRetentionPolicy(String retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    /**
     * 获取 InfluxDB client HTTP 日志级别。
     *
     * @return InfluxDB client HTTP 日志级别
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * 设置 InfluxDB client HTTP 日志级别。
     *
     * @param logLevel InfluxDB client HTTP 日志级别
     */
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * 获取 readTimeout 字段。
     *
     * @return readTimeout 字段
     */
    public Duration getReadTimeout() {
        return readTimeout;
    }

    /**
     * 设置 readTimeout 字段。
     *
     * @param readTimeout readTimeout 字段
     */
    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * 获取 writeTimeout 字段。
     *
     * @return writeTimeout 字段
     */
    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    /**
     * 设置 writeTimeout 字段。
     *
     * @param writeTimeout writeTimeout 字段
     */
    public void setWriteTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    /**
     * 获取 connectTimeout 字段。
     *
     * @return connectTimeout 字段
     */
    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 设置 connectTimeout 字段。
     *
     * @param connectTimeout connectTimeout 字段
     */
    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * 获取 batchSize 字段。
     *
     * @return batchSize 字段
     */
    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * 设置 batchSize 字段。
     *
     * @param batchSize batchSize 字段
     */
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * 获取 flushInterval 字段。
     *
     * @return flushInterval 字段
     */
    public Integer getFlushInterval() {
        return flushInterval;
    }

    /**
     * 设置 flushInterval 字段。
     *
     * @param flushInterval flushInterval 字段
     */
    public void setFlushInterval(Integer flushInterval) {
        this.flushInterval = flushInterval;
    }

    /**
     * 获取 jitterInterval 字段。
     *
     * @return jitterInterval 字段
     */
    public Integer getJitterInterval() {
        return jitterInterval;
    }

    /**
     * 设置 jitterInterval 字段。
     *
     * @param jitterInterval jitterInterval 字段
     */
    public void setJitterInterval(Integer jitterInterval) {
        this.jitterInterval = jitterInterval;
    }

    /**
     * 获取 retryInterval 字段。
     *
     * @return retryInterval 字段
     */
    public Integer getRetryInterval() {
        return retryInterval;
    }

    /**
     * 设置 retryInterval 字段。
     *
     * @param retryInterval retryInterval 字段
     */
    public void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval;
    }

    /**
     * 获取 maxRetries 字段。
     *
     * @return maxRetries 字段
     */
    public Integer getMaxRetries() {
        return maxRetries;
    }

    /**
     * 设置 maxRetries 字段。
     *
     * @param maxRetries maxRetries 字段
     */
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * 获取 maxRetryDelay 字段。
     *
     * @return maxRetryDelay 字段
     */
    public Integer getMaxRetryDelay() {
        return maxRetryDelay;
    }

    /**
     * 设置 maxRetryDelay 字段。
     *
     * @param maxRetryDelay maxRetryDelay 字段
     */
    public void setMaxRetryDelay(Integer maxRetryDelay) {
        this.maxRetryDelay = maxRetryDelay;
    }

    /**
     * 获取 maxRetryTime 字段。
     *
     * @return maxRetryTime 字段
     */
    public Integer getMaxRetryTime() {
        return maxRetryTime;
    }

    /**
     * 设置 maxRetryTime 字段。
     *
     * @param maxRetryTime maxRetryTime 字段
     */
    public void setMaxRetryTime(Integer maxRetryTime) {
        this.maxRetryTime = maxRetryTime;
    }

    /**
     * 获取 exponentialBase 字段。
     *
     * @return exponentialBase 字段
     */
    public Integer getExponentialBase() {
        return exponentialBase;
    }

    /**
     * 设置 exponentialBase 字段。
     *
     * @param exponentialBase exponentialBase 字段
     */
    public void setExponentialBase(Integer exponentialBase) {
        this.exponentialBase = exponentialBase;
    }

    /**
     * 获取 bufferLimit 字段。
     *
     * @return bufferLimit 字段
     */
    public Integer getBufferLimit() {
        return bufferLimit;
    }

    /**
     * 设置 bufferLimit 字段。
     *
     * @param bufferLimit bufferLimit 字段
     */
    public void setBufferLimit(Integer bufferLimit) {
        this.bufferLimit = bufferLimit;
    }

    /**
     * 获取 backpressureStrategy 字段。
     *
     * @return backpressureStrategy 字段
     */
    public BackpressureOverflowStrategy getBackpressureStrategy() {
        return backpressureStrategy;
    }

    /**
     * 设置 backpressureStrategy 字段。
     *
     * @param backpressureStrategy backpressureStrategy 字段
     */
    public void setBackpressureStrategy(BackpressureOverflowStrategy backpressureStrategy) {
        this.backpressureStrategy = backpressureStrategy;
    }
}
