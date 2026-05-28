package com.vanta.starter.data.enums;

/**
 * MyBatis-Plus ID 生成器类型枚举。
 * <p>
 * 该枚举用于配置 starter 应该注册哪一种 {@link com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator}。
 * </p>
 */
public enum MyBatisPlusIdGeneratorType {

    /**
     * 默认
     */
    DEFAULT,

    /**
     * CosId
     */
    COSID,

    /**
     * 自定义
     */
    CUSTOM
}
