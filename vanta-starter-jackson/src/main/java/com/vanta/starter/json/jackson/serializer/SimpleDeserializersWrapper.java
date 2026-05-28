package com.vanta.starter.json.jackson.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.type.ClassKey;


/**
 * SimpleDeserializersWrapper 类。
 * <p>该类型属于 JSON 序列化能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class SimpleDeserializersWrapper extends SimpleDeserializers {

    /**
     * 查询 JSON 序列化能力 数据。
     * 该方法封装底层查询细节，调用方只需要关注查询条件和返回结果。
     *
     * @param type     type 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @param config   config 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @param beanDesc beanDesc 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws JsonMappingException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public JsonDeserializer<?> findEnumDeserializer(Class<?> type,
                                                    DeserializationConfig config,
                                                    BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> deser = super.findEnumDeserializer(type, config, beanDesc);
        if (deser != null) {
            return deser;
        }
        // 重写增强：开始查找指定枚举类型的接口的反序列化器（例如：GenderEnum 枚举类型，则是找它的接口 BaseEnum 的反序列化器）
        for (Class<?> typeInterface : type.getInterfaces()) {
            deser = this._classMappings.get(new ClassKey(typeInterface));
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
}
