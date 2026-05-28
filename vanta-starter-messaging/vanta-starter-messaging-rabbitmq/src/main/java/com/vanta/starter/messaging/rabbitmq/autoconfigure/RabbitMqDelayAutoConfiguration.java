package com.vanta.starter.messaging.rabbitmq.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.messaging.rabbitmq.util.SpringBeanUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;


/**
 * RabbitMQ 延迟消息自动配置。
 *
 * <p>该配置使用 TTL 队列加死信交换机模拟延迟消息。只有
 * {@code vanta-starter.rabbitmq.delay.enabled=true} 时才会创建延迟交换机、计划交换机、完成交换机和各级 TTL 队列。</p>
 */
@AutoConfiguration(after = RabbitMqAutoConfiguration.class)
@ConditionalOnProperty(prefix = PropertiesConstants.RABBITMQ_DELAY, name = PropertiesConstants.ENABLED, havingValue = "true")
public class RabbitMqDelayAutoConfiguration {

    /**
     * RabbitMQ 延迟消息自动配置日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RabbitMqDelayAutoConfiguration.class);

    /**
     * RabbitMQ 延迟消息配置。
     */
    private final RabbitMqDelayProperties properties;

    /**
     * 创建 RabbitMQ 延迟消息自动配置。
     *
     * @param properties RabbitMQ 延迟消息配置
     */
    public RabbitMqDelayAutoConfiguration(RabbitMqDelayProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建延迟交换机。
     *
     * @return 延迟消息入口交换机
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(properties.getDelayExchange(), true, false);
    }

    /**
     * 创建计划交换机。
     *
     * <p>延迟队列消息到期后会通过死信配置进入该交换机。</p>
     *
     * @return 计划交换机
     */
    @Bean
    public TopicExchange planExchange() {
        return new TopicExchange(properties.getPlanExchange(), true, false);
    }

    /**
     * 创建完成交换机。
     *
     * @return 完成交换机
     */
    @Bean
    public TopicExchange finishExchange() {
        return new TopicExchange(properties.getFinishExchange(), true, false);
    }

    /**
     * 创建计划队列。
     *
     * @return 计划队列
     */
    @Bean
    public Queue planQueue() {
        return QueueBuilder.durable(properties.getPlanQueue()).build();
    }

    /**
     * 创建完成队列。
     *
     * @return 完成队列
     */
    @Bean
    public Queue finishQueue() {
        return QueueBuilder.durable(properties.getFinishQueue()).build();
    }

    /**
     * 绑定计划队列到计划交换机。
     *
     * @param planQueue    计划队列
     * @param planExchange 计划交换机
     * @return 计划绑定关系
     */
    @Bean
    public Binding planBinding(Queue planQueue, TopicExchange planExchange) {
        return BindingBuilder.bind(planQueue).to(planExchange).with(properties.getPlanRouting());
    }

    /**
     * 绑定完成队列到完成交换机。
     *
     * @param finishQueue    完成队列
     * @param finishExchange 完成交换机
     * @return 完成绑定关系
     */
    @Bean
    public Binding finishBinding(Queue finishQueue, TopicExchange finishExchange) {
        return BindingBuilder.bind(finishQueue).to(finishExchange).with(properties.getFinishRouting());
    }

    /**
     * 为每个延迟级别动态创建 TTL 队列和绑定。
     *
     * <p>每个 TTL 队列都会设置死信交换机和死信路由键，消息过期后进入计划队列继续处理。</p>
     *
     * @param delayExchange 延迟交换机
     * @return Spring Bean 占位对象，实际队列和绑定通过 {@link SpringBeanUtil} 动态注册
     */
    @Bean
    public Object delayExchanges(DirectExchange delayExchange) {
        for (Integer ttlSecond : properties.getDelayLevels()) {
            Queue delayQueue = QueueBuilder
                    .durable(properties.getDelayQueue4Second(ttlSecond))
                    .withArgument("x-message-ttl", ttlSecond * 1000)
                    .withArgument("x-dead-letter-exchange", properties.getPlanExchange())
                    .withArgument("x-dead-letter-routing-key", properties.getPlanRouting())
                    .build();

            Binding delayBinding = BindingBuilder
                    .bind(delayQueue)
                    .to(delayExchange)
                    .with(properties.getDelayRouting4Second(ttlSecond));

            SpringBeanUtil.registerBean("second%dDelayQueue".formatted(ttlSecond), delayQueue);
            SpringBeanUtil.registerBean("second%dDelayBinding".formatted(ttlSecond), delayBinding);
        }

        return null;
    }

    /**
     * 输出自动配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'RabbitMq Delay' completed initialization.");
    }

}
