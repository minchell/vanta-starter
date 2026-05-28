package com.vanta.starter.json.jackson.enums;


/**
 * BigNumberSerializeMode 枚举。
 * <p>该类型属于 JSON 序列化能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public enum BigNumberSerializeMode {

    /**
     * 超过 JS 范围的数值转为 {@link String} 类型，否则保持原类型
     * <p>
     * JS：Number.MIN_SAFE_INTEGER：-9007199254740991L <br />
     * JS：Number.MAX_SAFE_INTEGER：9007199254740991L
     * </p>
     */
    FLEXIBLE,

    /**
     * 统一转为 {@link String} 类型
     */
    TO_STRING,

    /**
     * 不操作（不建议）
     * <p>
     * 注意：超过 JS 范围的数值会损失精度，例如：8014753905961037835 会被转为 8014753905961038000
     * </p>
     */
    NO_OPERATE,
}
