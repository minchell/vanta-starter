package com.vanta.starter.json.jackson.serializer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.vanta.starter.core.enums.BaseEnum;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;


/**
 * BaseEnumDeserializer 类。
 * <p>该类型属于 JSON 序列化能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@JacksonStdImpl
public class BaseEnumDeserializer extends JsonDeserializer<BaseEnum> {

    /**
     * 静态实例
     */
    public static final BaseEnumDeserializer SERIALIZER_INSTANCE = new BaseEnumDeserializer();

    /**
     * 执行 deserialize 逻辑。
     * 该方法属于 JSON 序列化能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param jsonParser             jsonParser 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @param deserializationContext deserializationContext 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public BaseEnum deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext) throws IOException {
        Class<?> targetClass = jsonParser.currentValue().getClass();
        String fieldName = jsonParser.currentName();
        String value = jsonParser.getText();
        return this.getEnum(targetClass, value, fieldName);
    }

    /**
     * 通过某字段对应值获取枚举实例，获取不到时为 {@code null}
     *
     * @param targetClass 目标类型
     * @param value       字段值
     * @param fieldName   字段名
     * @return 对应枚举实例 ，获取不到时为 {@code null}
     */
    private BaseEnum getEnum(Class<?> targetClass, String value, String fieldName) {
        Field field = ReflectUtil.getField(targetClass, fieldName);
        Class<?> fieldTypeClass = field.getType();
        Object[] enumConstants = fieldTypeClass.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            if (ClassUtil.isAssignable(BaseEnum.class, fieldTypeClass)) {
                BaseEnum baseEnum = (BaseEnum) enumConstant;
                if (Objects.equals(Convert.toStr(baseEnum.getValue()), Convert.toStr(value))) {
                    return baseEnum;
                }
            }
        }
        return null;
    }
}
