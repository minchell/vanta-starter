package com.vanta.starter.json.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.vanta.starter.core.enums.BaseEnum;

import java.io.IOException;


/**
 * BaseEnumSerializer 类。
 * <p>该类型属于 JSON 序列化能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@JacksonStdImpl
public class BaseEnumSerializer extends JsonSerializer<BaseEnum> {

    /**
     * 静态实例
     */
    public static final BaseEnumSerializer SERIALIZER_INSTANCE = new BaseEnumSerializer();

    /**
     * 执行 serialize 逻辑。
     * 该方法属于 JSON 序列化能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param value       value 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @param generator   generator 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @param serializers serializers 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public void serialize(BaseEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeObject(value.getValue());
    }
}
