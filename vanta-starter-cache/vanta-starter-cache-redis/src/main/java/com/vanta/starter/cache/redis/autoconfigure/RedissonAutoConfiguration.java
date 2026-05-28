package com.vanta.starter.cache.redis.autoconfigure;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanta.starter.cache.redis.handler.NameMapperHandler;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.constant.StringConstants;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.List;

/**
 * Redisson 自动装配。
 *
 * <p>
 * 该配置只在 {@code spring.data.redisson.enabled=true} 时生效，负责创建 RedissonClient，
 * 并把 Spring Data Redis 的连接工厂切换为 RedissonConnectionFactory。这样 Redis starter
 * 不再依赖 Redisson 官方 starter 的无条件自动装配，业务项目可以通过一个明确开关控制是否启用远程 Redis 能力。
 * </p>
 */
@AutoConfiguration(before = org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@ConditionalOnProperty(prefix = "spring.data.redisson", name = PropertiesConstants.ENABLED, havingValue = "true")
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {

    /**
     * 当前自动装配类的日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(RedissonAutoConfiguration.class);

    /**
     * Redis 普通连接协议前缀。
     */
    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    /**
     * Redis TLS 连接协议前缀。
     */
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    /**
     * Redisson 扩展配置属性。
     */
    private final RedissonProperties properties;

    /**
     * Spring Boot Redis 标准配置属性。
     */
    private final RedisProperties redisProperties;

    /**
     * 用于 Redisson JSON codec 的 Jackson ObjectMapper。
     */
    private final ObjectMapper objectMapper;

    /**
     * 创建自动装配实例。
     *
     * @param properties      Redisson 扩展配置属性。
     * @param redisProperties Spring Boot Redis 标准配置属性。
     * @param objectMapper    用于序列化 Redis 值的 Jackson ObjectMapper。
     */
    public RedissonAutoConfiguration(RedissonProperties properties, RedisProperties redisProperties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.redisProperties = redisProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * 注册 RedissonClient 默认 Bean。
     *
     * <p>
     * RedissonClient 是 Redis 连接、分布式锁、限流、缓存等能力的基础客户端。
     * 业务项目如果需要完全替换 Redisson 客户端，可以自行声明同类型 Bean，本方法会自动让出。
     * </p>
     *
     * @return Redisson 客户端。
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient() {
        Config config = new Config();
        RedissonProperties.Mode mode = properties.getMode();
        String protocolPrefix = redisProperties.getSsl().isEnabled() ? REDISS_PROTOCOL_PREFIX : REDIS_PROTOCOL_PREFIX;
        switch (mode) {
            case CLUSTER -> this.buildClusterModeConfig(config, protocolPrefix);
            case SENTINEL -> this.buildSentinelModeConfig(config, protocolPrefix);
            default -> this.buildSingleModeConfig(config, protocolPrefix);
        }
        config.setCodec(new JsonJacksonCodec(objectMapper));
        log.debug("[Vanta Starter] - Auto Configuration 'Redisson' completed initialization.");
        return Redisson.create(config);
    }

    /**
     * 注册 RedisConnectionFactory 默认 Bean。
     *
     * <p>
     * Spring Data Redis 的 RedisTemplate、StringRedisTemplate、CosId Redis 机器号分配器都会依赖该连接工厂。
     * 当业务项目没有自定义 RedisConnectionFactory 时，starter 使用 RedissonConnectionFactory 统一承载连接。
     * </p>
     *
     * @param redissonClient Redisson 客户端。
     * @return Redisson Spring Data Redis 连接工厂。
     */
    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    /**
     * 构建 Redis Cluster 模式配置。
     *
     * @param config         Redisson 根配置对象。
     * @param protocolPrefix Redis 连接协议前缀。
     */
    private void buildClusterModeConfig(Config config, String protocolPrefix) {
        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        ClusterServersConfig customClusterServersConfig = properties.getClusterServersConfig();
        if (customClusterServersConfig != null) {
            BeanUtil.copyProperties(customClusterServersConfig, clusterServersConfig);
            clusterServersConfig.setNodeAddresses(customClusterServersConfig.getNodeAddresses());
        }

        // 下方配置如果为空，则使用 Redis 的配置
        if (CollUtil.isEmpty(clusterServersConfig.getNodeAddresses())) {
            List<String> nodeList = redisProperties.getCluster().getNodes();
            nodeList.stream().map(node -> protocolPrefix + node).forEach(clusterServersConfig::addNodeAddress);
        }

        // 兼容 Redis 没配置密码的情况
        if (CharSequenceUtil.isBlank(clusterServersConfig.getPassword())) {
            String password = redisProperties.getPassword();
            clusterServersConfig.setPassword(CharSequenceUtil.isNotBlank(password) ? password : null);
        }

        // Key 前缀
        if (CharSequenceUtil.isNotBlank(properties.getKeyPrefix())) {
            clusterServersConfig.setNameMapper(new NameMapperHandler(properties.getKeyPrefix()));
        }
    }

    /**
     * 构建 Redis Sentinel 模式配置。
     *
     * @param config         Redisson 根配置对象。
     * @param protocolPrefix Redis 连接协议前缀。
     */
    private void buildSentinelModeConfig(Config config, String protocolPrefix) {
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
        SentinelServersConfig customSentinelServersConfig = properties.getSentinelServersConfig();

        if (customSentinelServersConfig != null) {
            BeanUtil.copyProperties(customSentinelServersConfig, sentinelServersConfig);
            sentinelServersConfig.setSentinelAddresses(customSentinelServersConfig.getSentinelAddresses());
        }

        // 下方配置如果为空，则使用 Redis 的配置
        if (CollUtil.isEmpty(sentinelServersConfig.getSentinelAddresses())) {
            List<String> nodeList = redisProperties.getSentinel().getNodes();
            nodeList.stream().map(node -> protocolPrefix + node).forEach(sentinelServersConfig::addSentinelAddress);
        }

        // 兼容 Redis 没配置密码的情况
        if (CharSequenceUtil.isBlank(sentinelServersConfig.getPassword())) {
            String password = redisProperties.getPassword();
            sentinelServersConfig.setPassword(CharSequenceUtil.isNotBlank(password) ? password : null);
        }

        if (CharSequenceUtil.isBlank(sentinelServersConfig.getMasterName())) {
            sentinelServersConfig.setMasterName(redisProperties.getSentinel().getMaster());
        }

        // Key 前缀
        if (CharSequenceUtil.isNotBlank(properties.getKeyPrefix())) {
            sentinelServersConfig.setNameMapper(new NameMapperHandler(properties.getKeyPrefix()));
        }
    }

    /**
     * 构建 Redis 单机模式配置。
     *
     * @param config         Redisson 根配置对象。
     * @param protocolPrefix Redis 连接协议前缀。
     */
    private void buildSingleModeConfig(Config config, String protocolPrefix) {
        SingleServerConfig singleServerConfig = config.useSingleServer();
        SingleServerConfig customSingleServerConfig = properties.getSingleServerConfig();

        if (customSingleServerConfig != null) {
            BeanUtil.copyProperties(customSingleServerConfig, singleServerConfig);
        }

        // 下方配置如果为空，则使用 Redis 的配置
        singleServerConfig.setDatabase(redisProperties.getDatabase());

        // 兼容 Redis 没配置密码的情况
        if (CharSequenceUtil.isBlank(singleServerConfig.getPassword())) {
            String password = redisProperties.getPassword();
            singleServerConfig.setPassword(CharSequenceUtil.isNotBlank(password) ? password : null);
        }

        if (CharSequenceUtil.isBlank(singleServerConfig.getAddress())) {
            singleServerConfig.setAddress(protocolPrefix + redisProperties.getHost() + StringConstants.COLON + redisProperties.getPort());
        }

        // Key 前缀
        if (CharSequenceUtil.isNotBlank(properties.getKeyPrefix())) {
            singleServerConfig.setNameMapper(new NameMapperHandler(properties.getKeyPrefix()));
        }
    }

}
