package com.vanta.starter.core.exception;

import java.io.Serial;

/**
 * 请求参数或请求语义不合法异常。
 * <p>
 * 该异常用于表达调用方传入的数据不满足当前接口或组件约束，通常可被 Web 层转换为 400 类响应。
 * 类本身不依赖 Web 框架，便于在非 Web 场景中同样表达“请求不成立”的业务语义。
 * </p>
 */
public class BadRequestException extends BaseException {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建一个不携带消息和原因的请求异常。
     */
    public BadRequestException() {
    }

    /**
     * 创建一个携带错误消息的请求异常。
     *
     * @param message 请求不合法的具体说明。
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * 创建一个携带底层原因的请求异常。
     *
     * @param cause 触发当前异常的原始异常。
     */
    public BadRequestException(Throwable cause) {
        super(cause);
    }

    /**
     * 创建一个同时携带错误消息和底层原因的请求异常。
     *
     * @param message 请求不合法的具体说明。
     * @param cause   触发当前异常的原始异常。
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
