package com.vanta.starter.cache.springcache.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

class SpringCacheAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SpringCacheAutoConfiguration.class))
            .withBean(ObjectMapper.class, ObjectMapper::new)
            .withBean(CacheProperties.class, CacheProperties::new);

    @Test
    void shouldNotCreateSpringCacheInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(SpringCacheAutoConfiguration.class)
                .doesNotHaveBean(RedisCacheConfiguration.class)
                .doesNotHaveBean(KeyGenerator.class));
    }

    @Test
    void shouldCreateSpringCacheInfrastructureWhenExplicitlyEnabled() {
        contextRunner.withPropertyValues("vanta-starter.cache.spring-cache.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(SpringCacheAutoConfiguration.class)
                        .hasSingleBean(RedisCacheConfiguration.class)
                        .hasSingleBean(KeyGenerator.class));
    }
}
