package com.vanta.starter.messaging.rabbitmq.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * 最终投递给业务处理器的延迟消息。
 *
 * <p>该对象只承载事件名称、消息内容和创建时间戳，必须保持可序列化，方便通过 RabbitMQ 在多个队列之间流转。</p>
 */
public class DelayMessage implements Serializable {

    /**
     * Java 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务事件名称。
     *
     * <p>完成队列消费者会用该值查找对应的 {@code AbstractDelayMessageProcessor}。</p>
     */
    private String eventName;

    /**
     * 消息内容。
     *
     * <p>当前实现使用字符串承载消息，复杂对象建议业务方自行序列化为 JSON。</p>
     */
    private String message;

    /**
     * 消息创建时间戳，单位毫秒。
     */
    private Long timestamp;

    /**
     * 创建延迟消息。
     *
     * @param eventName 业务事件名称
     * @param message   消息内容
     */
    public DelayMessage(String eventName, String message) {
        this.eventName = eventName;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 获取业务事件名称。
     *
     * @return 业务事件名称
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * 设置业务事件名称。
     *
     * @param eventName 业务事件名称
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * 获取消息内容。
     *
     * @return 消息内容
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置消息内容。
     *
     * @param message 消息内容
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取消息创建时间戳，单位毫秒。
     *
     * @return 消息创建时间戳，单位毫秒
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置消息创建时间戳，单位毫秒。
     *
     * @param timestamp 消息创建时间戳，单位毫秒
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
