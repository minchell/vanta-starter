package com.vanta.starter.core.exception;

import java.io.Serial;

/**
 * Vanta Starter 基础运行时异常。
 * <p>
 * 该异常是 starter 内部可预期异常的公共父类，业务 starter 可以继承它来统一异常层级。
 * 它只承载异常信息和原始 cause，不绑定 Web、数据库或中间件语义，便于在任意 Spring Boot 项目中复用。
 * </p>
 */
public class BaseException extends RuntimeException {

    /**
     * 序列化版本号。
     * <p>
     * 异常对象可能被日志、远程调用框架或测试工具序列化，固定版本号可以减少 JVM 序列化兼容性噪声。
     * </p>
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建一个不携带消息和原因的基础异常。
     * <p>
     * 主要用于框架反射或子类在后续自行补充异常信息的场景。
     * </p>
     */
    public BaseException() {
    }

    /**
     * 创建一个携带错误消息的基础异常。
     *
     * @param message 面向调用方或日志的错误描述。
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * 创建一个携带底层原因的基础异常。
     *
     * @param cause 触发当前异常的原始异常。
     */
    public BaseException(Throwable cause) {
        super(cause);
    }

    /**
     * 创建一个同时携带错误消息和底层原因的基础异常。
     *
     * @param message 面向调用方或日志的错误描述。
     * @param cause   触发当前异常的原始异常。
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
