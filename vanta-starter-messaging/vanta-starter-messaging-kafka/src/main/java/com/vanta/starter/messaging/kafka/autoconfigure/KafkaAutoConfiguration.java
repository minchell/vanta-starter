package com.vanta.starter.messaging.kafka.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.messaging.core.spi.VantaMessageHeaderCustomizer;
import com.vanta.starter.messaging.core.spi.VantaMessageKeyResolver;
import com.vanta.starter.messaging.kafka.core.VantaKafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;

import java.util.List;

/**
 * Kafka 自动配置入口。
 *
 * <p>只有 classpath 中存在 Spring Kafka 且显式开启
 * {@code vanta-starter.kafka.enabled=true} 时才注册 Vanta Kafka 增强能力。</p>
 */
@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.KAFKA, name = PropertiesConstants.ENABLED, havingValue = "true")
public class KafkaAutoConfiguration {

    /**
     * 默认 Kafka 消费异常处理器 Bean 名称。
     * <p>
     * 固定 Bean 名称便于业务方通过同名 Bean 精准替换默认实现。
     * </p>
     */
    public static final String DEFAULT_ERROR_HANDLER_NAME = "vantaKafkaConsumerErrorHandler";

    /**
     * Kafka starter 自动配置日志门面。
     * <p>
     * 仅用于默认消费异常处理器记录消费失败信息。
     * </p>
     */
    private static final Logger log = LoggerFactory.getLogger(KafkaAutoConfiguration.class);

    /**
     * 注册默认消息 key 解析器。
     *
     * @return 默认读取 {@code VantaMessage.key()} 的 key 解析器
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaMessageKeyResolver kafkaMessageKeyResolver() {
        return message -> message == null ? null : message.key();
    }

    /**
     * 注册 Vanta Kafka 发送模板。
     *
     * @param kafkaTemplate     Spring Kafka 原生发送模板
     * @param keyResolver       消息 key 解析扩展点
     * @param headerCustomizers 消息头定制扩展点集合
     * @return Vanta Kafka 发送模板
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaKafkaTemplate vantaKafkaTemplate(KafkaTemplate<Object, Object> kafkaTemplate,
                                                 VantaMessageKeyResolver keyResolver,
                                                 List<VantaMessageHeaderCustomizer> headerCustomizers) {
        return new VantaKafkaTemplate(kafkaTemplate, keyResolver, headerCustomizers);
    }

    /**
     * 注册默认 Kafka 消费异常处理器。
     * <p>
     * 默认实现只记录本地错误日志并返回 {@code null}，不会提交远程告警或重试策略；
     * 业务方可以通过同名 Bean 替换为自己的错误处理流程。
     * </p>
     *
     * @return 默认 Kafka 消费异常处理器
     */
    @Bean(DEFAULT_ERROR_HANDLER_NAME)
    @ConditionalOnMissingBean(name = DEFAULT_ERROR_HANDLER_NAME)
    @ConditionalOnProperty(prefix = PropertiesConstants.KAFKA, name = "default-error-handler-enabled", havingValue = "true", matchIfMissing = true)
    public ConsumerAwareListenerErrorHandler vantaKafkaConsumerErrorHandler() {
        return (message, exception, consumer) -> {
            log.error("Kafka 消费异常，payload={}, exception={}", message.getPayload(), exception.getMessage(), exception);
            return null;
        };
    }
}
