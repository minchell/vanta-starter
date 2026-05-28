package com.vanta.starter.messaging.rocketmq.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.messaging.core.spi.VantaMessageHeaderCustomizer;
import com.vanta.starter.messaging.core.spi.VantaMessageKeyResolver;
import com.vanta.starter.messaging.rocketmq.core.VantaRocketMqTemplate;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * RocketMQ 自动配置入口。
 *
 * <p>只有 classpath 中存在 RocketMQTemplate 且显式开启
 * {@code vanta-starter.rocketmq.enabled=true} 时才注册 Vanta 增强模板。</p>
 */
@AutoConfiguration
@ConditionalOnClass(RocketMQTemplate.class)
@AutoConfigureAfter(name = "org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration")
@EnableConfigurationProperties(RocketMqProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.ROCKETMQ, name = PropertiesConstants.ENABLED, havingValue = "true")
public class RocketMqAutoConfiguration {

    /**
     * 注册默认消息 key 解析器。
     *
     * @return 默认读取 {@code VantaMessage.key()} 的 key 解析器
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaMessageKeyResolver rocketMqMessageKeyResolver() {
        return message -> message == null ? null : message.key();
    }

    /**
     * 注册 Vanta RocketMQ 发送模板。
     *
     * @param rocketMQTemplate  RocketMQ Spring 原生模板
     * @param properties        Vanta RocketMQ 增强配置
     * @param keyResolver       消息 key 解析扩展点
     * @param headerCustomizers 消息头定制扩展点集合
     * @return Vanta RocketMQ 发送模板
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaRocketMqTemplate vantaRocketMqTemplate(RocketMQTemplate rocketMQTemplate,
                                                       RocketMqProperties properties,
                                                       VantaMessageKeyResolver keyResolver,
                                                       List<VantaMessageHeaderCustomizer> headerCustomizers) {
        return new VantaRocketMqTemplate(rocketMQTemplate, properties, keyResolver, headerCustomizers);
    }
}
