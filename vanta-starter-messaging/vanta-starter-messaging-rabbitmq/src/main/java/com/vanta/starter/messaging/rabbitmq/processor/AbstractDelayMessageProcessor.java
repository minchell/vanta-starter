package com.vanta.starter.messaging.rabbitmq.processor;

import com.vanta.starter.messaging.rabbitmq.exception.RabbitMqException;
import com.vanta.starter.messaging.rabbitmq.util.RabbitMqSender;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 延迟消息业务处理器基类。
 *
 * <p>业务项目通过继承该类声明一个具体事件处理器。starter 在完成队列消费阶段会按
 * {@link #getEventName()} 查找处理器，并调用 {@link #processMessage(String, Long)} 完成业务处理。</p>
 */
public abstract class AbstractDelayMessageProcessor {

    /**
     * 延迟消息处理器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractDelayMessageProcessor.class);

    /**
     * RabbitMQ 发送工具。
     *
     * <p>子类可以直接调用 {@link #sendDelayMessage(String, int)} 继续投递同一事件的延迟消息。</p>
     */
    @Resource
    private RabbitMqSender rabbitMqSender;

    /**
     * 获取当前处理器负责的事件名称。
     *
     * @return 事件名称，必须与发送延迟消息时传入的 eventName 一致
     */
    public abstract String getEventName();

    /**
     * 处理最终到期的延迟消息。
     *
     * @param message   消息内容
     * @param timestamp 消息创建时间戳
     * @return true 表示处理成功并 ack，false 表示处理失败并 nack 重新入队
     */
    public abstract boolean processMessage(String message, Long timestamp);

    /**
     * 发送当前事件类型的延迟消息。
     *
     * @param message      消息内容
     * @param delaySeconds 延迟秒数
     * @throws RabbitMqException 延迟能力未开启或无法生成延迟计划时抛出
     */
    public void sendDelayMessage(String message, int delaySeconds) throws RabbitMqException {
        rabbitMqSender.sendDelayMessage(getEventName(), message, delaySeconds);
    }

    /**
     * 消息处理成功回调。
     *
     * @param message   消息内容
     * @param timestamp 消息创建时间戳
     */
    public void onMessageSuccess(String message, Long timestamp) {
        // 可被子类重写
    }

    /**
     * 消息处理失败回调。
     *
     * @param message   消息内容
     * @param timestamp 消息创建时间戳
     */
    public void onMessageFailure(String message, Long timestamp) {
        // 可被子类重写
    }

    /**
     * 消息处理异常回调。
     *
     * @param message   消息内容
     * @param timestamp 消息创建时间戳
     * @param e         处理过程中抛出的异常
     */
    public void onMessageError(String message, Long timestamp, Exception e) {
        // 可被子类重写
    }

}

