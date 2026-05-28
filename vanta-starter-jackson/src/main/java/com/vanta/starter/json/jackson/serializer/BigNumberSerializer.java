package com.vanta.starter.json.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import java.io.IOException;


/**
 * BigNumberSerializer 类。
 * <p>该类型属于 JSON 序列化能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@JacksonStdImpl
public class BigNumberSerializer extends NumberSerializer {

    /**
     * 静态实例
     */
    public static final BigNumberSerializer SERIALIZER_INSTANCE = new BigNumberSerializer(Number.class);
    /**
     * JS：Number.MAX_SAFE_INTEGER
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    /**
     * JS：Number.MIN_SAFE_INTEGER
     */
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param rawType rawType 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     */
    public BigNumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    /**
     * 执行 serialize 逻辑。
     * 该方法属于 JSON 序列化能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param value    value 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @param gen      gen 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @param provider provider 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
            super.serialize(value, gen, provider);
        } else {
            gen.writeString(value.toString());
        }
    }
}
