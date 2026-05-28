package com.vanta.starter.auth.justauth.autoconfigure;

import com.vanta.starter.auth.justauth.AuthRequestFactory;
import me.zhyd.oauth.cache.AuthStateCache;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class JustAuthAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JustAuthAutoConfiguration.class));

    @Test
    void shouldNotCreateJustAuthInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(JustAuthAutoConfiguration.class)
                .doesNotHaveBean(AuthRequestFactory.class)
                .doesNotHaveBean(AuthStateCache.class));
    }

    @Test
    void shouldCreateJustAuthInfrastructureWhenExplicitlyEnabled() {
        contextRunner.withPropertyValues("vanta-starter.justauth.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(JustAuthAutoConfiguration.class)
                        .hasSingleBean(AuthRequestFactory.class)
                        .hasSingleBean(AuthStateCache.class));
    }
}
