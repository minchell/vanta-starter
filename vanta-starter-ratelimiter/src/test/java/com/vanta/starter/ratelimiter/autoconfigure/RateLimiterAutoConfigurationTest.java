package com.vanta.starter.ratelimiter.autoconfigure;

import com.vanta.starter.ratelimiter.aop.RateLimiterAspect;
import com.vanta.starter.ratelimiter.generator.RateLimiterNameGenerator;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RateLimiterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RateLimiterAutoConfiguration.class))
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class));

    @Test
    void shouldNotCreateRateLimiterInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(RateLimiterAutoConfiguration.class)
                .doesNotHaveBean(RateLimiterAspect.class)
                .doesNotHaveBean(RateLimiterNameGenerator.class));
    }

    @Test
    void shouldCreateRateLimiterInfrastructureWhenExplicitlyEnabled() {
        contextRunner.withPropertyValues("vanta-starter.rate-limiter.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(RateLimiterAutoConfiguration.class)
                        .hasSingleBean(RateLimiterAspect.class)
                        .hasSingleBean(RateLimiterNameGenerator.class));
    }
}
