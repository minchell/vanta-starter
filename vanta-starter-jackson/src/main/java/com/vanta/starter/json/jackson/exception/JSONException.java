package com.vanta.starter.json.jackson.exception;

import com.vanta.starter.core.exception.BaseException;

import java.io.Serial;


/**
 * JSONException 类。
 * <p>该类型属于 JSON 序列化能力，负责表达当前 starter 的领域异常，便于调用方区分错误来源。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class JSONException extends BaseException {

    /**
     * serialVersionUID 字段。
     * <p>用于保存 JSON 序列化能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     */
    public JSONException() {
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param message message 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     */
    public JSONException(String message) {
        super(message);
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param cause cause 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     */
    public JSONException(Throwable cause) {
        super(cause);
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param message message 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @param cause   cause 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     */
    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }
}
