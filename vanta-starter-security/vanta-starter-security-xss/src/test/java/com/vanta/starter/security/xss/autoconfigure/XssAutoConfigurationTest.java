package com.vanta.starter.security.xss.autoconfigure;

import com.vanta.starter.security.xss.filter.XssFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

class XssAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(XssAutoConfiguration.class));

    @Test
    void shouldNotCreateXssInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(XssAutoConfiguration.class)
                .doesNotHaveBean(XssFilter.class)
                .doesNotHaveBean(FilterRegistrationBean.class));
    }

    @Test
    void shouldCreateXssInfrastructureWhenExplicitlyEnabled() {
        contextRunner.withPropertyValues("vanta-starter.security.xss.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(XssAutoConfiguration.class)
                        .hasSingleBean(FilterRegistrationBean.class));
    }
}
