package com.vanta.starter.cache.redis.autoconfigure;

import org.redisson.config.ClusterServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * RedissonProperties 类。
 * <p>该类型属于 缓存能力，负责承载 Spring Boot 配置绑定参数，并为自动配置提供可读的开关和连接参数。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@ConfigurationProperties("spring.data.redisson")
public class RedissonProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 缓存键前缀
     */
    private String keyPrefix;

    /**
     * Redis 模式
     */
    private Mode mode = Mode.SINGLE;

    /**
     * 单机服务配置
     */
    @NestedConfigurationProperty
    private SingleServerConfig singleServerConfig;

    /**
     * 集群服务配置
     */
    @NestedConfigurationProperty
    private ClusterServersConfig clusterServersConfig;

    /**
     * 哨兵服务配置
     */
    @NestedConfigurationProperty
    private SentinelServersConfig sentinelServersConfig;

    /**
     * 获取是否启用。
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用。
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取缓存键前缀。
     *
     * @return 缓存键前缀
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * 设置缓存键前缀。
     *
     * @param keyPrefix 缓存键前缀
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    /**
     * 获取 Redis 模式。
     *
     * @return Redis 模式
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * 设置 Redis 模式。
     *
     * @param mode Redis 模式
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * 获取单机服务配置。
     *
     * @return 单机服务配置
     */
    public SingleServerConfig getSingleServerConfig() {
        return singleServerConfig;
    }

    /**
     * 设置单机服务配置。
     *
     * @param singleServerConfig 单机服务配置
     */
    public void setSingleServerConfig(SingleServerConfig singleServerConfig) {
        this.singleServerConfig = singleServerConfig;
    }

    /**
     * 获取集群服务配置。
     *
     * @return 集群服务配置
     */
    public ClusterServersConfig getClusterServersConfig() {
        return clusterServersConfig;
    }

    /**
     * 设置集群服务配置。
     *
     * @param clusterServersConfig 集群服务配置
     */
    public void setClusterServersConfig(ClusterServersConfig clusterServersConfig) {
        this.clusterServersConfig = clusterServersConfig;
    }

    /**
     * 获取哨兵服务配置。
     *
     * @return 哨兵服务配置
     */
    public SentinelServersConfig getSentinelServersConfig() {
        return sentinelServersConfig;
    }

    /**
     * 设置哨兵服务配置。
     *
     * @param sentinelServersConfig 哨兵服务配置
     */
    public void setSentinelServersConfig(SentinelServersConfig sentinelServersConfig) {
        this.sentinelServersConfig = sentinelServersConfig;
    }

    /**
     * Redis 模式
     */
    public enum Mode {
        /**
         * 单机
         */
        SINGLE,

        /**
         * 集群
         */
        CLUSTER,

        /**
         * 哨兵
         */
        SENTINEL
    }
}
