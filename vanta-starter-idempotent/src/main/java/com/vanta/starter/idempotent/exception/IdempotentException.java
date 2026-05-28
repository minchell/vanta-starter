package com.vanta.starter.idempotent.exception;

import com.vanta.starter.core.exception.BaseException;


/**
 * IdempotentException 类。
 * <p>该类型属于 幂等能力，负责表达当前 starter 的领域异常，便于调用方区分错误来源。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class IdempotentException extends BaseException {

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param message message 参数，调用方应传入与 幂等能力 场景匹配的有效值
     */
    public IdempotentException(String message) {
        super(message);
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param message message 参数，调用方应传入与 幂等能力 场景匹配的有效值
     * @param cause   cause 参数，调用方应传入与 幂等能力 场景匹配的有效值
     */
    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }
}
