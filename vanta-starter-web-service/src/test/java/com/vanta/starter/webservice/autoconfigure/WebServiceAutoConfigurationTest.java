package com.vanta.starter.webservice.autoconfigure;

import com.feiniaojin.gracefulresponse.advice.GrNotVoidResponseBodyAdvice;
import com.vanta.starter.apidoc.autoconfigure.SpringDocAutoConfiguration;
import com.vanta.starter.validation.autoconfigure.ValidationAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

class WebServiceAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class,
                    HttpMessageConvertersAutoConfiguration.class,
                    HttpEncodingAutoConfiguration.class,
                    WebMvcAutoConfiguration.class,
                    WebServiceAutoConfiguration.class
            ));

    @Test
    void shouldEnableBasicWebServiceInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .hasSingleBean(WebServiceAutoConfiguration.class)
                .hasSingleBean(ValidationAutoConfiguration.class)
                .hasSingleBean(CorsFilter.class)
                .hasSingleBean(GrNotVoidResponseBodyAdvice.class));
    }

    @Test
    void shouldBackOffWhenWebServiceIsDisabled() {
        contextRunner.withPropertyValues("vanta-starter.web-service.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(WebServiceAutoConfiguration.class));
    }

    @Test
    void shouldDisableResponseInfrastructureWhenResponseIsDisabled() {
        contextRunner.withPropertyValues("vanta-starter.web-service.response.enabled=false")
                .run(context -> assertThat(context)
                        .hasSingleBean(WebServiceAutoConfiguration.class)
                        .doesNotHaveBean(GrNotVoidResponseBodyAdvice.class));
    }

    @Test
    void shouldDisableApiDocInfrastructureWhenApiDocIsDisabled() {
        contextRunner.withPropertyValues("vanta-starter.web-service.api-doc.enabled=false")
                .run(context -> assertThat(context)
                        .hasSingleBean(WebServiceAutoConfiguration.class)
                        .doesNotHaveBean(SpringDocAutoConfiguration.class));
    }
}
