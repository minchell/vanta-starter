package com.vanta.starter.messaging.rocketmq.core;

import com.vanta.starter.messaging.core.model.VantaMessage;
import com.vanta.starter.messaging.core.model.VantaSendOptions;
import com.vanta.starter.messaging.core.model.VantaSendResult;
import com.vanta.starter.messaging.core.spi.VantaMessageHeaderCustomizer;
import com.vanta.starter.messaging.core.spi.VantaMessageKeyResolver;
import com.vanta.starter.messaging.rocketmq.autoconfigure.RocketMqProperties;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Vanta RocketMQ 发送模板。
 *
 * <p>该模板吸收 giftcard 旧 starter 中增强发送、延迟发送、事务发送的思路，
 * 但不再要求业务消息继承固定父类。调用方可以传普通对象，也可以传
 * {@link VantaMessage} 携带 key、source 和 headers。</p>
 */
public class VantaRocketMqTemplate {

    /**
     * 统一发送结果中的中间件名称。
     */
    private static final String PROVIDER = "rocketmq";

    /**
     * RocketMQ Spring 原生模板。
     * <p>底层 name server、producer group、序列化器等仍由业务项目的 RocketMQ 原生配置决定。</p>
     */
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * Vanta RocketMQ 增强配置。
     * <p>只控制默认 tag、发送超时和 Vanta 封装是否启用，不复制 RocketMQ 官方配置空间。</p>
     */
    private final RocketMqProperties properties;

    /**
     * 消息 key 解析器。
     * <p>用于把通用消息信封映射为 RocketMQ KEYS 头，业务方可以替换为领域化 key 策略。</p>
     */
    private final VantaMessageKeyResolver keyResolver;

    /**
     * 消息头定制扩展点集合。
     * <p>发送前依次调用，用于统一追加 traceId、tenantId、source 等跨系统排障字段。</p>
     */
    private final List<VantaMessageHeaderCustomizer> headerCustomizers;

    /**
     * 创建 Vanta RocketMQ 发送模板。
     *
     * @param rocketMQTemplate  RocketMQ Spring 原生模板
     * @param properties        Vanta RocketMQ 增强配置
     * @param keyResolver       消息 key 解析器
     * @param headerCustomizers 消息头定制扩展点集合
     */
    public VantaRocketMqTemplate(RocketMQTemplate rocketMQTemplate,
                                 RocketMqProperties properties,
                                 VantaMessageKeyResolver keyResolver,
                                 List<VantaMessageHeaderCustomizer> headerCustomizers) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.properties = properties;
        this.keyResolver = keyResolver;
        this.headerCustomizers = headerCustomizers == null ? List.of() : List.copyOf(headerCustomizers);
    }

    /**
     * 发送普通消息。
     *
     * @param topic   RocketMQ topic
     * @param payload 业务消息体
     * @return 统一发送结果
     */
    public VantaSendResult send(String topic, Object payload) {
        return send(topic, VantaMessage.of(payload), VantaSendOptions.defaults());
    }

    /**
     * 发送带 Vanta 消息信封和发送选项的消息。
     *
     * @param topic   RocketMQ topic
     * @param message Vanta 通用消息信封
     * @param options 发送选项；为空时使用默认选项
     * @return 统一发送结果
     */
    public VantaSendResult send(String topic, VantaMessage<?> message, VantaSendOptions options) {
        try {
            VantaSendOptions actualOptions = options == null ? VantaSendOptions.defaults() : options;
            Message<?> springMessage = buildSpringMessage(message, actualOptions);
            String destination = buildDestination(topic, actualOptions.tag());
            long timeout = actualOptions.timeout() == null
                    ? properties.getSendTimeout().toMillis()
                    : actualOptions.timeout().toMillis();

            SendResult sendResult;
            if (actualOptions.delayLevel() != null && actualOptions.delayLevel() > 0) {
                sendResult = rocketMQTemplate.syncSend(destination, springMessage, timeout, actualOptions.delayLevel());
            } else {
                sendResult = rocketMQTemplate.syncSend(destination, springMessage, timeout);
            }
            return VantaSendResult.success(PROVIDER, sendResult.getMsgId(), sendResult.getSendStatus().name());
        } catch (Exception ex) {
            return VantaSendResult.failure(PROVIDER, ex);
        }
    }

    /**
     * 发送 RocketMQ 事务消息。
     *
     * @param topic   RocketMQ topic
     * @param message Vanta 通用消息信封
     * @param arg     本地事务监听器需要的业务参数
     * @return 统一发送结果
     */
    public VantaSendResult sendInTransaction(String topic, VantaMessage<?> message, Object arg) {
        try {
            Message<?> springMessage = buildSpringMessage(message, VantaSendOptions.defaults());
            TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction(buildDestination(topic, null), springMessage, arg);
            return VantaSendResult.success(PROVIDER, result.getMsgId(), result.getSendStatus().name());
        } catch (Exception ex) {
            return VantaSendResult.failure(PROVIDER, ex);
        }
    }

    /**
     * 构建 Spring Messaging 消息对象。
     *
     * @param message Vanta 通用消息信封
     * @param options 发送选项
     * @return RocketMQTemplate 可发送的 Spring 消息对象
     */
    private Message<?> buildSpringMessage(VantaMessage<?> message, VantaSendOptions options) {
        Map<String, Object> headers = new LinkedHashMap<>(message.headers());
        headers.putAll(options.headers());
        if (StringUtils.hasText(message.source())) {
            headers.put("source", message.source());
        }
        headerCustomizers.forEach(customizer -> customizer.customize(message, headers));

        MessageBuilder<?> builder = MessageBuilder.withPayload(message.payload());
        String messageKey = keyResolver.resolveKey(message);
        if (StringUtils.hasText(messageKey)) {
            builder.setHeader(RocketMQHeaders.KEYS, messageKey);
        }
        headers.forEach(builder::setHeader);
        return builder.build();
    }

    /**
     * 构建 RocketMQ destination。
     *
     * @param topic RocketMQ topic
     * @param tag   消息 tag，空值时使用默认 tag
     * @return RocketMQTemplate 需要的 {@code topic:tag} 字符串
     */
    private String buildDestination(String topic, String tag) {
        String actualTag = StringUtils.hasText(tag) ? tag : properties.getDefaultTag();
        return topic + ":" + actualTag;
    }
}
