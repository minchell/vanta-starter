package com.vanta.starter.cache.redis.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanta.starter.cache.redis.api.RedisCache;
import com.vanta.starter.cache.redis.api.StrRedisCache;
import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisAutoConfiguration 类。
 * <p>该类型属于 缓存能力，负责根据 classpath、配置开关和缺省 Bean 条件装配 starter 默认能力。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@AutoConfiguration(before = RedissonAutoConfiguration.class)
@ConditionalOnProperty(prefix = "spring.data.redisson", name = PropertiesConstants.ENABLED, havingValue = "true")
public class RedisAutoConfiguration {

    /**
     * 注册 Object> 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     *
     * @param redisConnectionFactory redisConnectionFactory 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param objectMapper           objectMapper 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jsonSerializer);

        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 注册 RedisCache 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     *
     * @param redisTemplate redisTemplate 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Bean
    public RedisCache redisCache(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCache(redisTemplate);
    }

    /**
     * 注册 StrRedisCache 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     *
     * @param stringRedisTemplate stringRedisTemplate 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Bean
    public StrRedisCache strRedisCache(StringRedisTemplate stringRedisTemplate) {
        return new StrRedisCache(stringRedisTemplate);
    }

}
