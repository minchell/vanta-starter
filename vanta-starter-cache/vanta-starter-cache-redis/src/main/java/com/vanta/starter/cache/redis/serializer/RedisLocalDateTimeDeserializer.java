package com.vanta.starter.cache.redis.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;

import java.io.IOException;
import java.io.Serial;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RedisLocalDateTimeDeserializer 类。
 * <p>该类型属于 缓存能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class RedisLocalDateTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalDateTime> {

    /**
     * INSTANCE 字段。
     * <p>用于保存 缓存能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    public static final RedisLocalDateTimeDeserializer INSTANCE = new RedisLocalDateTimeDeserializer();
    /**
     * serialVersionUID 字段。
     * <p>用于保存 缓存能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * DEFAULT_FORMATTER 字段。
     * <p>用于保存 缓存能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    protected RedisLocalDateTimeDeserializer() { // was private before 2.12
        this(DEFAULT_FORMATTER);
    }

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param formatter formatter 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public RedisLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    /**
     * Since 2.10
     */
    protected RedisLocalDateTimeDeserializer(RedisLocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    /**
     * 执行 withDateFormat 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param formatter formatter 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    protected RedisLocalDateTimeDeserializer withDateFormat(DateTimeFormatter formatter) {
        return new RedisLocalDateTimeDeserializer(formatter);
    }

    /**
     * 执行 withLeniency 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param leniency leniency 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    protected RedisLocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new RedisLocalDateTimeDeserializer(this, leniency);
    }

    /**
     * 执行 withShape 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param shape shape 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    protected RedisLocalDateTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
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
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            String string0 = parser.getText().trim();
            if (string0.isEmpty()) {
                return _fromEmptyString(parser, context, string0);
            }
            return convertToLocalDateTime(string0);
            // return _fromString(parser, context, parser.getText());
        }

        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (parser.isExpectedStartObjectToken()) {
            return _fromString(parser, context, context.extractScalarFromObject(parser, this, handledType()));
        }

        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
                    && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final LocalDateTime parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                LocalDateTime result;

                int year = parser.getIntValue();
                int month = parser.nextIntValue(-1);
                int day = parser.nextIntValue(-1);
                int hour = parser.nextIntValue(-1);
                int minute = parser.nextIntValue(-1);

                t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute);
                } else {
                    int second = parser.getIntValue();
                    t = parser.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second);
                    } else {
                        int partialSecond = parser.getIntValue();
                        if (partialSecond < 1_000 &&
                                !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))
                            partialSecond *= 1_000_000; // value is milliseconds, convert it to nanoseconds
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY,
                                    "Expected array to end");
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                    }
                }
                return result;
            }
            context.reportInputMismatch(handledType(),
                    "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                    t);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDateTime) parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            _throwNoNumericTimestampNeedTimeZone(parser, context);
        }
        return _handleUnexpectedToken(context, parser, "Expected array or string.");
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
    protected LocalDateTime _fromString(JsonParser p, DeserializationContext dctx, String string0) throws IOException {
        String string = string0.trim();
        if (string.isEmpty()) {
            // 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
            //   b/w empty and blank); for now don't which will allow blanks to be
            //   handled like "regular" empty (same as pre-2.12)
            return _fromEmptyString(p, dctx, string);
        }
        try {
            // 21-Oct-2020, tatu: Changed as per [modules-core#94] for 2.12,
            //    had bad timezone handle change from [modules-core#56]
            if (_formatter == DEFAULT_FORMATTER) {
                // ... only allow iff lenient mode enabled since
                // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                // And if so, do NOT use zoned date parsing as that can easily produce
                // incorrect answer.
                if (string.length() > 10 && string.charAt(10) == 'T') {
                    if (string.endsWith("Z")) {
                        if (isLenient()) {
                            return LocalDateTime.parse(string.substring(0, string.length() - 1),
                                    _formatter);
                        }
                        JavaType t = getValueType(dctx);
                        return (LocalDateTime) dctx.handleWeirdStringValue(t.getRawClass(),
                                string,
                                "Should not contain offset when 'strict' mode set for property or type (enable 'lenient' handling to allow)"
                        );
                    }
                }
            }
            return LocalDateTime.parse(string, _formatter);
        } catch (DateTimeException e) {
            return _handleDateTimeException(dctx, e, string);
        }
    }

    /**
     * 转换 convert To Local Date Time 的输入数据。
     * 该方法负责在底层客户端模型和业务可读模型之间做边界转换，避免调用方直接依赖底层细节。
     *
     * @param string0 string0 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public LocalDateTime convertToLocalDateTime(String string0) {
        string0 = string0.trim();
        if (string0.isEmpty()) {
            return null;
        }

        if (string0.matches("^\\d{4}-\\d{1,2}$")) {
            // yyyy-MM
            return LocalDateTime.parse(string0 + "-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        if (string0.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            // yyyy-MM-dd
            return LocalDateTime.parse(string0 + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        if (string0.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            // yyyy-MM-dd HH:mm
            return LocalDateTime.parse(string0 + ":00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        if (string0.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            // yyyy-MM-dd HH:mm:ss
            return LocalDateTime.parse(string0, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        throw new IllegalArgumentException("Invalid datetime value '" + string0 + "'");
    }
}
