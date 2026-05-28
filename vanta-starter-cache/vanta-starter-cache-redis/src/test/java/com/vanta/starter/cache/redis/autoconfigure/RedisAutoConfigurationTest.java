package com.vanta.starter.cache.redis.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanta.starter.cache.redis.api.RedisCache;
import com.vanta.starter.cache.redis.api.StrRedisCache;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RedisAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RedisAutoConfiguration.class, RedissonAutoConfiguration.class))
            .withBean(ObjectMapper.class, ObjectMapper::new)
            .withBean(RedisProperties.class, RedisProperties::new);

    @Test
    void shouldNotCreateRedisInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(RedisAutoConfiguration.class)
                .doesNotHaveBean(RedissonAutoConfiguration.class)
                .doesNotHaveBean(RedissonClient.class)
                .doesNotHaveBean(RedissonConnectionFactory.class)
                .doesNotHaveBean(RedisCache.class)
                .doesNotHaveBean(StrRedisCache.class));
    }

    @Test
    void shouldCreateRedisHelpersWhenExplicitlyEnabledAndClientIsProvided() {
        contextRunner
                .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
                .withBean(StringRedisTemplate.class, () -> mock(StringRedisTemplate.class))
                .withPropertyValues("spring.data.redisson.enabled=true")
                .run(context -> {
                    assertThat(context)
                            .hasSingleBean(RedisAutoConfiguration.class)
                            .hasSingleBean(RedissonAutoConfiguration.class)
                            .hasSingleBean(RedissonConnectionFactory.class)
                            .hasSingleBean(RedisCache.class)
                            .hasSingleBean(StrRedisCache.class);
                    assertThat(context).hasBean("redisTemplate");
                    assertThat(context.getBean("redisTemplate")).isInstanceOf(RedisTemplate.class);
                    assertThat(context).hasSingleBean(StringRedisTemplate.class);
                });
    }
}
