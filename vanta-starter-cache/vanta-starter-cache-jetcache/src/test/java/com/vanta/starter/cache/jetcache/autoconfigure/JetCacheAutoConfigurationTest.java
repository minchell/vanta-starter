package com.vanta.starter.cache.jetcache.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class JetCacheAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JetCacheAutoConfiguration.class));

    @Test
    void shouldNotCreateJetCacheInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(JetCacheAutoConfiguration.class));
    }

    @Test
    void shouldCreateJetCacheInfrastructureWhenExplicitlyEnabled() {
        contextRunner.withPropertyValues("vanta-starter.cache.jetcache.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(JetCacheAutoConfiguration.class));
    }
}
