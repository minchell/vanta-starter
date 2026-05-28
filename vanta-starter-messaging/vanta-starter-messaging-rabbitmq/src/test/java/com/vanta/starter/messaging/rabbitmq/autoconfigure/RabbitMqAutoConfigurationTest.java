package com.vanta.starter.messaging.rabbitmq.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RabbitMqAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RabbitMqAutoConfiguration.class))
            .withBean(ConnectionFactory.class, () -> mock(ConnectionFactory.class));

    @Test
    void shouldNotCreateRabbitInfrastructureByDefault() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(RabbitMqAutoConfiguration.class)
                .doesNotHaveBean(RabbitAdmin.class)
                .doesNotHaveBean(RabbitTemplate.class));
    }

    @Test
    void shouldCreateRabbitInfrastructureWhenExplicitlyEnabled() {
        contextRunner
                .withPropertyValues("vanta-starter.rabbitmq.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(RabbitMqAutoConfiguration.class)
                        .hasSingleBean(RabbitAdmin.class)
                        .hasSingleBean(RabbitTemplate.class));
    }
}
