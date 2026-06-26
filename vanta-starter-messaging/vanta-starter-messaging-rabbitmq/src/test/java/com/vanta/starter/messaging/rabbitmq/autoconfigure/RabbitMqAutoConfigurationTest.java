package com.vanta.starter.messaging.rabbitmq.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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

    @Test
    void shouldRegisterConfiguredRabbitDeclarablesWhenEnabled() {
        contextRunner
                .withPropertyValues(
                        "vanta-starter.rabbitmq.enabled=true",
                        "vanta-starter.rabbitmq.exchanges[0].name=vanta.test.events",
                        "vanta-starter.rabbitmq.exchanges[0].type=DIRECT",
                        "vanta-starter.rabbitmq.queues[0].name=vanta.test.queue",
                        "vanta-starter.rabbitmq.queues[0].exchange-name=vanta.test.events",
                        "vanta-starter.rabbitmq.queues[0].routing-key=vanta.test.key")
                .run(context -> {
                    assertThat(context).hasBean("vanta.test.eventsExchange");
                    assertThat(context).hasBean("vanta.test.queueQueue");
                    assertThat(context).hasBean("vanta.test.queuevanta.test.eventsBinding");
                    assertThat(context.getBean("vanta.test.eventsExchange")).isInstanceOf(DirectExchange.class);
                    assertThat(context.getBean("vanta.test.queueQueue")).isInstanceOf(Queue.class);
                    assertThat(context.getBean("vanta.test.queuevanta.test.eventsBinding")).isInstanceOf(Binding.class);
                });
    }
}
