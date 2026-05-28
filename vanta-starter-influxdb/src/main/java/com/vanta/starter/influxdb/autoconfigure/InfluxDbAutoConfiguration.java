package com.vanta.starter.influxdb.autoconfigure;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.influxdb.core.VantaInfluxTemplate;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * InfluxDB 自动配置入口。
 *
 * <p>默认不启用远程连接。只有显式开启 {@code vanta-starter.influxdb.enabled=true}
 * 后才会创建 InfluxDBClient 和 WriteApi。</p>
 */
@AutoConfiguration
@ConditionalOnClass(InfluxDBClient.class)
@EnableConfigurationProperties(InfluxDbProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.INFLUXDB, name = PropertiesConstants.ENABLED, havingValue = "true")
public class InfluxDbAutoConfiguration {

    /**
     * 将空字符串配置兼容为 InfluxDB 1.x 鉴权需要的空值片段。
     *
     * @param value 原始配置值
     * @return 非 null 字符串
     */
    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * 注册 InfluxDB 官方客户端。
     * <p>
     * 默认只有显式启用 starter 且业务方未提供客户端 Bean 时才创建；启用后必须配置 URL，
     * 避免引入依赖就产生远程连接副作用。
     * </p>
     *
     * @param properties InfluxDB starter 配置
     * @return InfluxDB 官方客户端
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public InfluxDBClient influxDBClient(InfluxDbProperties properties) {
        if (!StringUtils.hasText(properties.getUrl())) {
            throw new IllegalStateException("vanta-starter.influxdb.url must be configured when InfluxDB starter is enabled");
        }

        InfluxDBClientOptions.Builder builder = InfluxDBClientOptions.builder()
                .okHttpClient(okHttpClientBuilder(properties))
                .url(properties.getUrl());

        if (StringUtils.hasText(properties.getDatabase())) {
            String token = nullToEmpty(properties.getUsername()) + ":" + nullToEmpty(properties.getPassword());
            String bucket = properties.getDatabase() + "/" + nullToEmpty(properties.getRetentionPolicy());
            builder.org("-").bucket(bucket).authenticateToken(token.toCharArray()).consistency(null);
        } else if (StringUtils.hasText(properties.getToken())) {
            builder.org(properties.getOrg()).bucket(properties.getBucket()).authenticateToken(properties.getToken().toCharArray());
        }

        return InfluxDBClientFactory.create(builder.build())
                .setLogLevel(properties.getLogLevel())
                .enableGzip();
    }

    /**
     * 注册 InfluxDB 异步写入 API。
     *
     * @param influxDBClient InfluxDB 官方客户端
     * @param properties     InfluxDB 写入批处理配置
     * @return InfluxDB 异步写入 API
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public WriteApi writeApi(InfluxDBClient influxDBClient, InfluxDbProperties properties) {
        WriteOptions.Builder builder = WriteOptions.builder()
                .batchSize(properties.getBatchSize())
                .flushInterval(properties.getFlushInterval())
                .jitterInterval(properties.getJitterInterval())
                .retryInterval(properties.getRetryInterval())
                .maxRetries(properties.getMaxRetries())
                .maxRetryDelay(properties.getMaxRetryDelay())
                .maxRetryTime(properties.getMaxRetryTime())
                .exponentialBase(properties.getExponentialBase())
                .bufferLimit(properties.getBufferLimit())
                .backpressureStrategy(properties.getBackpressureStrategy());
        return influxDBClient.makeWriteApi(builder.build());
    }

    /**
     * 注册 Vanta InfluxDB 操作模板。
     *
     * @param influxDBClient InfluxDB 官方客户端
     * @param writeApi       InfluxDB 异步写入 API
     * @return Vanta InfluxDB 操作模板
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaInfluxTemplate vantaInfluxTemplate(InfluxDBClient influxDBClient, WriteApi writeApi) {
        return new VantaInfluxTemplate(influxDBClient, writeApi);
    }

    /**
     * 构建 InfluxDB 客户端使用的 OkHttp 配置。
     *
     * @param properties InfluxDB 连接超时配置
     * @return OkHttp 客户端构建器
     */
    private OkHttpClient.Builder okHttpClientBuilder(InfluxDbProperties properties) {
        return new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .readTimeout(properties.getReadTimeout())
                .writeTimeout(properties.getWriteTimeout())
                .connectTimeout(properties.getConnectTimeout());
    }
}
