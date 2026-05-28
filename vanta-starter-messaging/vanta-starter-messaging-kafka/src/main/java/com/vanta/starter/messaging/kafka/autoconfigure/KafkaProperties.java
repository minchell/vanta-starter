package com.vanta.starter.messaging.kafka.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Vanta Kafka 增强配置。
 *
 * <p>Kafka broker、producer、consumer 的原生参数仍使用 {@code spring.kafka.*}。
 * 本配置只控制 Vanta 发送模板、默认错误处理和消息头增强行为。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.KAFKA)
public class KafkaProperties {

    /**
     * 是否启用 Vanta Kafka 增强封装。
     * <p>true 表示注册 Vanta Kafka 模板，false 表示不注册
     */
    private boolean enabled = false;

    /**
     * 是否注册默认消费异常处理器。
     * <p>true 表示注册默认异常处理器，false 表示完全交给业务方配置</p>
     */
    private boolean defaultErrorHandlerEnabled = true;

    /**
     * 获取是否启用 Vanta Kafka 增强封装。
     *
     * @return 是否启用 Vanta Kafka 增强封装
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 Vanta Kafka 增强封装。
     *
     * @param enabled 是否启用 Vanta Kafka 增强封装
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取是否注册默认消费异常处理器。
     *
     * @return 是否注册默认消费异常处理器
     */
    public boolean isDefaultErrorHandlerEnabled() {
        return defaultErrorHandlerEnabled;
    }

    /**
     * 设置是否注册默认消费异常处理器。
     *
     * @param defaultErrorHandlerEnabled 是否注册默认消费异常处理器
     */
    public void setDefaultErrorHandlerEnabled(boolean defaultErrorHandlerEnabled) {
        this.defaultErrorHandlerEnabled = defaultErrorHandlerEnabled;
    }
}
