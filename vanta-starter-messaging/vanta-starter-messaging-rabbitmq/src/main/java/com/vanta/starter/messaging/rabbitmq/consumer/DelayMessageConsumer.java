package com.vanta.starter.messaging.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.messaging.rabbitmq.autoconfigure.RabbitMqDelayAutoConfiguration;
import com.vanta.starter.messaging.rabbitmq.autoconfigure.RabbitMqDelayProperties;
import com.vanta.starter.messaging.rabbitmq.model.DelayMessage;
import com.vanta.starter.messaging.rabbitmq.model.DelayModel;
import com.vanta.starter.messaging.rabbitmq.processor.AbstractDelayMessageProcessor;
import com.vanta.starter.messaging.rabbitmq.util.SpringBeanUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * RabbitMQ 延迟消息消费者。
 *
 * <p>该消费者监听计划队列和完成队列：计划队列负责按分段延迟计划继续投递，
 * 完成队列负责把最终消息分发给业务方实现的 {@link AbstractDelayMessageProcessor}。</p>
 */
@Component
@AutoConfigureAfter({RabbitMqDelayAutoConfiguration.class})
@ConditionalOnProperty(prefix = PropertiesConstants.RABBITMQ_DELAY, name = PropertiesConstants.ENABLED, havingValue = "true")
public class DelayMessageConsumer {

    /**
     * 延迟消息消费者日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DelayMessageConsumer.class);

    /**
     * RabbitMQ 消息发送模板。
     *
     * <p>用于把未完成的延迟计划继续投递到下一段 TTL 队列，或把最终消息投递到完成交换机。</p>
     */
    private final AmqpTemplate amqpTemplate;

    /**
     * RabbitMQ 延迟消息配置。
     */
    private final RabbitMqDelayProperties properties;
    /**
     * 延迟事件处理器索引。
     *
     * <p>key 是业务方处理器声明的事件名称，value 是对应处理器实例。</p>
     */
    private final Map<String, AbstractDelayMessageProcessor> processorMap = new ConcurrentHashMap<>(8);

    /**
     * 创建延迟消息消费者。
     *
     * @param amqpTemplate RabbitMQ 消息发送模板
     * @param properties   RabbitMQ 延迟消息配置
     */
    protected DelayMessageConsumer(AmqpTemplate amqpTemplate, RabbitMqDelayProperties properties) {
        this.amqpTemplate = amqpTemplate;
        this.properties = properties;
    }

    /**
     * 初始化延迟事件处理器索引。
     *
     * <p>启动时从 Spring 容器收集所有 {@link AbstractDelayMessageProcessor}，
     * 后续完成队列消费时按事件名称分发。</p>
     */
    @PostConstruct
    public void init() {
        var map = SpringBeanUtil.getBeansOfType(AbstractDelayMessageProcessor.class);

        if (!map.isEmpty()) {
            var retMap = map.values().stream()
                    .collect(Collectors.toMap(
                            AbstractDelayMessageProcessor::getEventName,
                            Function.identity(),
                            (a, b) -> a)
                    );
            processorMap.putAll(retMap);
        }
        log.debug("[Vanta Starter] - Auto Configuration 'RabbitMq DelayMessageConsumer' completed initialization.");
    }

    /**
     * 消费计划队列消息并推进下一段延迟计划。
     *
     * @param delayModel 延迟消息执行计划
     * @param channel    RabbitMQ 原生信道，用于手动确认
     * @param tag        当前投递标签
     */
    @RabbitListener(queues = "#{planQueue.name}")
    public void onPlan(DelayModel delayModel, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        List<Integer> executePlan = delayModel.getExecutePlans();
        Integer step = delayModel.getCurrentStep();
        boolean continueNext = executePlan.size() > step;

        log.info("延迟计划执行{}: {}", continueNext ? "中" : "完", delayModel.getDelayMessage());

        if (continueNext) {
            Integer nextDelaySecond = delayModel.getExecutePlans().get(step);
            delayModel.setCurrentStep(++step);
            this.amqpTemplate.convertAndSend(properties.getDelayExchange(), properties.getDelayRouting4Second(nextDelaySecond), delayModel);
        } else {
            this.amqpTemplate.convertAndSend(properties.getFinishExchange(), properties.getFinishRouting(), delayModel.getDelayMessage());
        }

        try {
            channel.basicAck(tag, false);
        } catch (IOException e) {
            log.error("onPlan#basicAck has error", e);
        }
    }

    /**
     * 消费完成队列消息并调用业务处理器。
     *
     * @param delayMessage 已到期的业务延迟消息
     * @param channel      RabbitMQ 原生信道，用于手动确认或拒绝
     * @param tag          当前投递标签
     */
    @RabbitListener(queues = "#{finishQueue.name}")
    public void onFinish(DelayMessage delayMessage, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        if (delayMessage == null) {
            rejectMessage(channel, tag, "delay message is null");
            return;
        }

        AbstractDelayMessageProcessor processor = processorMap.get(delayMessage.getEventName());
        if (processor == null) {
            rejectMessage(channel, tag, "delay message processor not found: " + delayMessage.getEventName());
            return;
        }

        String message = delayMessage.getMessage();
        Long timestamp = delayMessage.getTimestamp();

        try {
            boolean success = processor.processMessage(message, timestamp);
            if (success) {
                channel.basicAck(tag, false);
                processor.onMessageSuccess(message, timestamp);
            } else {
                channel.basicNack(tag, false, true);
                processor.onMessageFailure(message, timestamp);
            }
        } catch (Exception e) {
            log.error("onFinish dealing delay message has error: {}, {}", message, timestamp, e);
            processor.onMessageError(message, timestamp, e);
        }
    }

    /**
     * 拒绝无法处理的完成队列消息。
     *
     * <p>缺少消息体或缺少业务处理器都不是瞬时错误，继续 requeue 会造成重复消费；
     * 因此这里拒绝且不重新入队，让 RabbitMQ 按队列死信配置或默认策略处理。</p>
     *
     * @param channel RabbitMQ 原生信道
     * @param tag     当前投递标签
     * @param reason  拒绝原因
     */
    private void rejectMessage(Channel channel, long tag, String reason) {
        log.error("RabbitMQ delay message rejected, reason={}", reason);
        try {
            channel.basicNack(tag, false, false);
        } catch (IOException e) {
            log.error("RabbitMQ delay message basicNack failed, reason={}", reason, e);
        }
    }

}
