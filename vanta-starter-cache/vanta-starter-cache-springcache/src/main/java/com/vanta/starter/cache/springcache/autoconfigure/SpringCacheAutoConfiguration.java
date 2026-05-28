package com.vanta.starter.cache.springcache.autoconfigure;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.util.GeneralPropertySourceFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

/**
 * SpringCacheAutoConfiguration 类。
 * <p>该类型属于 缓存能力，负责根据 classpath、配置开关和缺省 Bean 条件装配 starter 默认能力。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = PropertiesConstants.CACHE_SPRING_CACHE, name = PropertiesConstants.ENABLED, havingValue = "true")
@PropertySource(value = "classpath:default-cache-springcache.yml", factory = GeneralPropertySourceFactory.class)
public class SpringCacheAutoConfiguration implements CachingConfigurer {

    /**
     * log 字段。
     * <p>用于保存 缓存能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(SpringCacheAutoConfiguration.class);

    /**
     * objectMapper 字段。
     * <p>用于保存 缓存能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final ObjectMapper objectMapper;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param objectMapper objectMapper 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public SpringCacheAutoConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Redis 缓存配置
     *
     * <p>解决 Spring Cache（@Cacheable）Jackson 解析缓存乱码问题</p>
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        ObjectMapper objectMapperCopy = objectMapper.copy();
        objectMapperCopy.activateDefaultTyping(objectMapperCopy
                .getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapperCopy)));
        CacheProperties.Redis redisCacheProperties = cacheProperties.getRedis();
        if (redisCacheProperties.getTimeToLive() != null) {
            redisCacheConfiguration = redisCacheConfiguration.entryTtl(redisCacheProperties.getTimeToLive());
        }
        if (!redisCacheProperties.isCacheNullValues()) {
            redisCacheConfiguration = redisCacheConfiguration.disableCachingNullValues();
        }
        return redisCacheConfiguration;
    }

    /**
     * 自定义缓存 key 生成策略
     *
     * <p>
     * 如果 @Cacheable 不指定 key，则默认使用该策略
     * </p>
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            String key = CharSequenceUtil.toUnderlineCase(method.getName()).toUpperCase();
            Map<String, Object> paramMap = MapUtil.newHashMap(params.length);
            for (int i = 0; i < params.length; i++) {
                paramMap.put(String.valueOf(i), params[i]);
            }
            return "%s:%s".formatted(key, DigestUtil.sha256Hex(JSONUtil.toJsonStr(paramMap)));
        };
    }

    /**
     * 执行 postConstruct 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Spring Cache' completed initialization.");
    }
}
