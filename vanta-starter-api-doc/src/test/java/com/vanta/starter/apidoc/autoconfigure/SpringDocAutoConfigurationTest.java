package com.vanta.starter.apidoc.autoconfigure;

import com.vanta.starter.core.autoconfigure.application.ApplicationAutoConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SpringDocAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    ApplicationAutoConfiguration.class,
                    SpringDocAutoConfiguration.class
            ));

    @Test
    void shouldNotEnableApiDocByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(SpringDocAutoConfiguration.class)
                .doesNotHaveBean(OpenAPI.class));
    }

    @Test
    void shouldEnableApiDocWhenConfigured() {
        contextRunner.withPropertyValues("vanta-starter.api-doc.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(SpringDocAutoConfiguration.class)
                        .hasSingleBean(OpenAPI.class));
    }
}
