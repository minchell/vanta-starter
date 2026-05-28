package com.vanta.starter.cache.redis.serializer;


import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;

import java.io.IOException;
import java.io.Serial;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * RedisLocalDateDeserializer 类。
 * <p>该类型属于 缓存能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class RedisLocalDateDeserializer extends JSR310DateTimeDeserializerBase<LocalDate> {

    /**
     * INSTANCE 字段。
     * <p>用于保存 缓存能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    public static final RedisLocalDateDeserializer INSTANCE;
    /**
     * serialVersionUID 字段。
     * <p>用于保存 缓存能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    @Serial
    private static final long serialVersionUID = 455430372610233949L;
    /**
     * DEFAULT_FORMATTER 字段。
     * <p>用于保存 缓存能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final DateTimeFormatter DEFAULT_FORMATTER;

    static {
        DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
        INSTANCE = new RedisLocalDateDeserializer();
    }

    protected RedisLocalDateDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param dtf dtf 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public RedisLocalDateDeserializer(DateTimeFormatter dtf) {
        super(LocalDate.class, dtf);
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param base base 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param dtf  dtf 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public RedisLocalDateDeserializer(RedisLocalDateDeserializer base, DateTimeFormatter dtf) {
        super(base, dtf);
    }

    protected RedisLocalDateDeserializer(RedisLocalDateDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    protected RedisLocalDateDeserializer(RedisLocalDateDeserializer base, Shape shape) {
        super(base, shape);
    }

    /**
     * 执行 withDateFormat 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param dtf dtf 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    protected RedisLocalDateDeserializer withDateFormat(DateTimeFormatter dtf) {
        return new RedisLocalDateDeserializer(this, dtf);
    }

    /**
     * 执行 withLeniency 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param leniency leniency 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    protected RedisLocalDateDeserializer withLeniency(Boolean leniency) {
        return new RedisLocalDateDeserializer(this, leniency);
    }

    /**
     * 执行 withShape 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param shape shape 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    protected RedisLocalDateDeserializer withShape(Shape shape) {
        return new RedisLocalDateDeserializer(this, shape);
    }

    /**
     * 执行 deserialize 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param parser  parser 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param context context 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            return this._fromString(parser, context, parser.getText());
        } else if (parser.isExpectedStartObjectToken()) {
            return this._fromString(parser, context, context.extractScalarFromObject(parser, this, this.handledType()));
        } else {
            if (parser.isExpectedStartArrayToken()) {
                JsonToken t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    return null;
                }

                if (context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS) && (t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)) {
                    LocalDate parsed = this.deserialize(parser, context);
                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                        this.handleMissingEndArrayForSingle(parser, context);
                    }

                    return parsed;
                }

                if (t == JsonToken.VALUE_NUMBER_INT) {
                    int year = parser.getIntValue();
                    int month = parser.nextIntValue(-1);
                    int day = parser.nextIntValue(-1);
                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                        throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
                    }

                    return LocalDate.of(year, month, day);
                }

                context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
            }

            if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
                return (LocalDate) parser.getEmbeddedObject();
            } else if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return this._shape != Shape.NUMBER_INT && !this.isLenient() ? this._failForNotLenient(parser, context, JsonToken.VALUE_STRING) : LocalDate.ofEpochDay(parser.getLongValue());
            } else {
                return this._handleUnexpectedToken(context, parser, "Expected array or string.", new Object[0]);
            }
        }
    }

    /**
     * 执行 _fromString 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param p       p 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param dctx    dctx 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param string0 string0 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    protected LocalDate _fromString(JsonParser p, DeserializationContext dctx, String string0) throws IOException {
        String string = string0.trim();
        if (string.isEmpty()) {
            return this._fromEmptyString(p, dctx, string);
        } else {
            try {
                DateTimeFormatter format = this._formatter;
                if (format == DEFAULT_FORMATTER) {
                    if (this.isLenient()) {
                        return string.endsWith("Z") ? LocalDate.parse(string.substring(0, string.length() - 1), DateTimeFormatter.ISO_LOCAL_DATE_TIME) : LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } else {
                        JavaType t = this.getValueType(dctx);
                        return (LocalDate) dctx.handleWeirdStringValue(t.getRawClass(), string, "Should not contain time component when 'strict' mode set for property or type (enable 'lenient' handling to allow)", new Object[0]);
                    }
                }
                return LocalDate.parse(string, format);
            } catch (DateTimeException var7) {
                return this._handleDateTimeException(dctx, var7, string);
            }
        }
    }
}
