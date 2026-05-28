package com.vanta.starter.messaging.rabbitmq.exception;

import com.vanta.starter.core.exception.BaseException;


/**
 * RabbitMQ starter 领域异常。
 *
 * <p>用于表达延迟消息未开启、延迟计划无法生成等 starter 自身可识别错误，方便调用方区别于底层 AMQP 异常。</p>
 */
public class RabbitMqException extends BaseException {

    /**
     * 创建 RabbitMQ starter 异常。
     *
     * @param message 异常消息
     */
    public RabbitMqException(String message) {
        super(message);
    }

    /**
     * 创建 RabbitMQ starter 异常。
     *
     * @param message 异常消息
     * @param cause   原始异常
     */
    public RabbitMqException(String message, Throwable cause) {
        super(message, cause);
    }
}
