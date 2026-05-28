package com.vanta.starter.data.autoconfigure.idgenerator;

import com.vanta.starter.data.enums.MyBatisPlusIdGeneratorType;

/**
 * MyBatis-Plus ID 生成器配置属性。
 * <p>
 * 该配置作为 {@code mybatis-plus.extension.id-generator} 的嵌套属性，
 * 用于选择默认雪花、CosId 或业务自定义 ID 生成器。
 * </p>
 */
public class MyBatisPlusIdGeneratorProperties {

    /**
     * ID 生成器类型
     */
    private MyBatisPlusIdGeneratorType type = MyBatisPlusIdGeneratorType.DEFAULT;

    /**
     * 获取 ID 生成器类型。
     *
     * @return ID 生成器类型
     */
    public MyBatisPlusIdGeneratorType getType() {
        return type;
    }

    /**
     * 设置 ID 生成器类型。
     *
     * @param type ID 生成器类型
     */
    public void setType(MyBatisPlusIdGeneratorType type) {
        this.type = type;
    }
}
