package com.vanta.starter.log.autoconfigure;

import com.vanta.starter.log.aspect.AccessLogAspect;
import com.vanta.starter.log.aspect.LogAspect;
import com.vanta.starter.log.dao.LogDao;
import com.vanta.starter.log.filter.LogFilter;
import com.vanta.starter.log.handler.LogHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

class LogAopAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LogAopAutoConfiguration.class));

    @Test
    void shouldNotCreateLogAopInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(LogAopAutoConfiguration.class)
                .doesNotHaveBean(LogFilter.class)
                .doesNotHaveBean(LogAspect.class)
                .doesNotHaveBean(AccessLogAspect.class)
                .doesNotHaveBean(LogHandler.class)
                .doesNotHaveBean(LogDao.class));
    }

    @Test
    void shouldCreateLogAopInfrastructureWhenExplicitlyEnabled() {
        contextRunner.withPropertyValues("vanta-starter.log.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(LogAopAutoConfiguration.class)
                        .hasSingleBean(FilterRegistrationBean.class)
                        .hasSingleBean(LogAspect.class)
                        .hasSingleBean(AccessLogAspect.class)
                        .hasSingleBean(LogHandler.class)
                        .hasSingleBean(LogDao.class));
    }
}
