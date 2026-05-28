package com.vanta.starter.messaging.rabbitmq.util;

import com.vanta.starter.messaging.rabbitmq.autoconfigure.RabbitMqAutoConfiguration;
import com.vanta.starter.messaging.rabbitmq.autoconfigure.RabbitMqDelayProperties;
import com.vanta.starter.messaging.rabbitmq.exception.RabbitMqException;
import com.vanta.starter.messaging.rabbitmq.model.DelayMessage;
import com.vanta.starter.messaging.rabbitmq.model.DelayModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import java.util.ArrayList;
import java.util.List;

/**
 * RabbitMQ 消息发送工具。
 *
 * <p>该类封装普通消息、点对点队列消息、批量消息和 TTL 分段延迟消息发送逻辑。
 * 它不隐藏底层 {@link AmqpTemplate} 和 {@link RabbitTemplate}，业务方仍可以获取底层模板处理更复杂的场景。</p>
 */
@AutoConfigureAfter({RabbitMqAutoConfiguration.class})
public class RabbitMqSender {

    /**
     * RabbitMQ 发送工具日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RabbitMqSender.class);

    /**
     * Spring AMQP 通用发送模板。
     *
     * <p>普通发送和延迟发送默认通过该模板完成。</p>
     */
    private final AmqpTemplate amqpTemplate;

    /**
     * RabbitMQ 专用发送模板。
     *
     * <p>保留给需要 publisher confirm、return callback 等 RabbitTemplate 专属能力的业务方使用。</p>
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 延迟消息配置。
     *
     * <p>用于生成延迟队列、路由键和分段执行计划。</p>
     */
    private final RabbitMqDelayProperties properties;

    /**
     * 创建 RabbitMQ 发送工具。
     *
     * @param amqpTemplate   Spring AMQP 通用发送模板
     * @param rabbitTemplate RabbitMQ 专用发送模板
     * @param properties     延迟消息配置
     */
    public RabbitMqSender(AmqpTemplate amqpTemplate, RabbitTemplate rabbitTemplate, RabbitMqDelayProperties properties) {
        this.amqpTemplate = amqpTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    /**
     * 向指定交换机和路由键发送普通消息。
     *
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param message    消息内容
     */
    public void send(String exchange, String routingKey, Object message) {
        amqpTemplate.convertAndSend(exchange, routingKey, message);
        log.debug("rabbitmq send message, exchange:{}, routingKey:{}, message:{}", exchange, routingKey, message);
    }

    /**
     * 直接向指定队列发送消息。
     *
     * <p>该方法使用默认交换机，因此 routingKey 等于队列名称。</p>
     *
     * @param queueName 队列名称
     * @param message   消息内容
     */
    public void send(String queueName, Object message) {
        amqpTemplate.convertAndSend("", queueName, message);
        log.debug("rabbitmq send message, queueName:{}, message:{}", queueName, message);
    }

    /**
     * 发送分段 TTL 延迟消息。
     *
     * <p>方法会把目标延迟秒数拆分为多个配置好的延迟级别，并将第一段计划投递到对应 TTL 队列。
     * 后续消费和重新投递由延迟消息消费者继续推进。</p>
     *
     * @param eventName    延迟事件名称
     * @param message      消息内容
     * @param delaySeconds 期望延迟秒数
     * @throws RabbitMqException 延迟能力未开启或无法生成延迟计划时抛出
     */
    public void sendDelayMessage(String eventName, String message, int delaySeconds) throws RabbitMqException {
        if (!properties.isEnabled()) {
            throw new RabbitMqException("The delayed messaging feature is not turned on");
        }

        DelayMessage delayMessage = new DelayMessage(eventName, message);

        List<Integer> plans = createPlans(delaySeconds);
        if (plans.isEmpty()) {
            throw new RabbitMqException("Generating delay plan failed::" + delaySeconds);
        }

        DelayModel delayModel = new DelayModel(delayMessage, plans, 1);
        Integer firstDelaySecond = delayModel.getExecutePlans().get(0);

        this.amqpTemplate.convertAndSend(
                properties.getDelayExchange(),
                properties.getDelayRouting4Second(firstDelaySecond),
                delayModel
        );
    }

    /**
     * 批量向指定交换机和路由键发送消息。
     *
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @param messages   消息集合
     */
    public void batchSend(String exchange, String routingKey, Iterable<?> messages) {
        for (Object message : messages) {
            send(exchange, routingKey, message);
        }
    }

    /**
     * 将目标延迟秒数拆分为多个可执行延迟级别。
     *
     * <p>例如配置延迟级别为 60、30、10 秒时，100 秒会拆分为 60、30、10。
     * 这里使用贪心算法，优先使用较大的延迟级别，减少中间轮转次数。</p>
     *
     * @param seconds 目标延迟秒数
     * @return 从大到小排列的延迟执行计划
     */
    private List<Integer> createPlans(Integer seconds) {
        List<Integer> secondsList = properties.getDelayLevels();
        List<Integer> result = new ArrayList<>();

        for (int i = secondsList.size() - 1; i > -1; ) {
            if (seconds >= secondsList.get(i)) {
                result.add(secondsList.get(i));
                seconds -= secondsList.get(i);
            } else {
                i--;
            }
        }

        return result;
    }

    /**
     * 获取 Spring AMQP 通用发送模板。
     *
     * @return Spring AMQP 通用发送模板
     */
    public AmqpTemplate getAmqpTemplate() {
        return amqpTemplate;
    }

    /**
     * 获取 RabbitMQ 专用发送模板。
     *
     * @return RabbitMQ 专用发送模板
     */
    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }
}
