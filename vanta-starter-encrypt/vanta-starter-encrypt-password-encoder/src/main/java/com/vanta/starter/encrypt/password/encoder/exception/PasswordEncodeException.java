package com.vanta.starter.encrypt.password.encoder.exception;

import com.vanta.starter.core.exception.BaseException;

import java.io.Serial;

/**
 * 密码编码异常
 */
public class PasswordEncodeException extends BaseException {

    /**
     * serialVersionUID 字段。
     * <p>用于保存 加密能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     */
    public PasswordEncodeException() {
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param message message 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public PasswordEncodeException(String message) {
        super(message);
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param cause cause 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public PasswordEncodeException(Throwable cause) {
        super(cause);
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param message message 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param cause   cause 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public PasswordEncodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
