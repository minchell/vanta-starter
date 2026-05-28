package com.vanta.starter.security.mask.core;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.security.mask.annotation.JsonMask;
import com.vanta.starter.security.mask.strategy.IMaskStrategy;

import java.io.IOException;
import java.util.Objects;

/**
 * JSON 脱敏序列化器
 */
public class JsonMaskSerializer extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * jsonMask 字段。
     * <p>用于保存 安全防护能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private JsonMask jsonMask;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param jsonMask jsonMask 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public JsonMaskSerializer(JsonMask jsonMask) {
        this.jsonMask = jsonMask;
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     */
    public JsonMaskSerializer() {
    }

    /**
     * 执行 serialize 逻辑。
     * 该方法属于 安全防护能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param str                str 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @param jsonGenerator      jsonGenerator 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @param serializerProvider serializerProvider 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public void serialize(String str, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (CharSequenceUtil.isBlank(str)) {
            jsonGenerator.writeString(StringConstants.EMPTY);
            return;
        }
        // 使用自定义脱敏策略
        Class<? extends IMaskStrategy> strategyClass = jsonMask.strategy();
        IMaskStrategy maskStrategy = strategyClass != IMaskStrategy.class
                ? SpringUtil.getBean(strategyClass)
                : jsonMask.value();
        jsonGenerator.writeString(maskStrategy.mask(str, jsonMask.character(), jsonMask.left(), jsonMask.right()));
    }

    /**
     * 构建 create Contextual 需要的对象。
     * 该方法将配置、参数或上下文转换为底层客户端可识别的结构，避免转换逻辑散落在业务代码中。
     *
     * @param serializerProvider serializerProvider 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @param beanProperty       beanProperty 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws JsonMappingException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider,
                                              BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty == null) {
            return serializerProvider.findNullValueSerializer(null);
        }
        if (!Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        JsonMask jsonMaskAnnotation = ObjectUtil.defaultIfNull(beanProperty.getAnnotation(JsonMask.class), beanProperty
                .getContextAnnotation(JsonMask.class));
        if (jsonMaskAnnotation == null) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return new JsonMaskSerializer(jsonMaskAnnotation);
    }
}
