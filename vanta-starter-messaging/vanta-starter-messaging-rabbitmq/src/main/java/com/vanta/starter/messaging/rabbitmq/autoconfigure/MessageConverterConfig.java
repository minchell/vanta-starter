package com.vanta.starter.messaging.rabbitmq.autoconfigure;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息转换器配置。
 *
 * <p>默认提供 Jackson JSON 消息转换器；业务项目声明自己的 {@link MessageConverter} 后会覆盖该默认实现。</p>
 */
@Configuration
public class MessageConverterConfig {

    /**
     * 注册默认 JSON 消息转换器。
     *
     * @return Jackson JSON 消息转换器
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }
}
