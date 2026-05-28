package com.vanta.starter.json.jackson.autoconfigure;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.vanta.starter.core.enums.BaseEnum;
import com.vanta.starter.core.util.DateUtils;
import com.vanta.starter.core.util.GeneralPropertySourceFactory;
import com.vanta.starter.json.jackson.serializer.BaseEnumDeserializer;
import com.vanta.starter.json.jackson.serializer.BaseEnumSerializer;
import com.vanta.starter.json.jackson.serializer.BigNumberSerializer;
import com.vanta.starter.json.jackson.serializer.SimpleDeserializersWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


/**
 * JacksonAutoConfiguration 类。
 * <p>该类型属于 JSON 序列化能力，负责根据 classpath、配置开关和缺省 Bean 条件装配 starter 默认能力。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(JacksonExtensionProperties.class)
@PropertySource(value = "classpath:default-json-jackson.yml", factory = GeneralPropertySourceFactory.class)
public class JacksonAutoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 JSON 序列化能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(JacksonAutoConfiguration.class);

    /**
     * properties 字段。
     * <p>用于保存 JSON 序列化能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final JacksonExtensionProperties properties;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param properties properties 参数，调用方应传入与 JSON 序列化能力 场景匹配的有效值
     */
    public JacksonAutoConfiguration(JacksonExtensionProperties properties) {
        this.properties = properties;
    }

    /**
     * 注册 Jackson2ObjectMapperBuilderCustomizer 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            JavaTimeModule javaTimeModule = this.javaTimeModule();
            SimpleModule baseEnumModule = this.baseEnumModule();
            SimpleModule bigNumberModule = this.bigNumberModule();

            builder.timeZone(TimeZone.getTimeZone(DateUtils.CN_ZONE));
            builder.modules(javaTimeModule, baseEnumModule, bigNumberModule);
            log.debug("[Vanta Starter] - Auto Configuration 'Jackson' completed initialization.");
        };
    }

    /**
     * 日期时间序列化及反序列化配置
     *
     * @return {@link JavaTimeModule}
     * @since 1.0.0
     */
    private JavaTimeModule javaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 针对时间类型：LocalDateTime 的序列化和反序列化处理
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        // 针对时间类型：LocalDate 的序列化和反序列化处理
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        // 针对时间类型：LocalTime 的序列化和反序列化处理
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN);
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        // 针对时间类型：Instant 的序列化和反序列化处理
        javaTimeModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        javaTimeModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        // 针对时间类型：Duration 的序列化和反序列化处理
        javaTimeModule.addSerializer(Duration.class, DurationSerializer.INSTANCE);
        javaTimeModule.addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
        return javaTimeModule;
    }

    /**
     * 枚举序列化及反序列化配置
     *
     * @return SimpleModule /
     * @since 2.4.0
     */
    private SimpleModule baseEnumModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BaseEnum.class, BaseEnumSerializer.SERIALIZER_INSTANCE);
        SimpleDeserializersWrapper deserializers = new SimpleDeserializersWrapper();
        deserializers.addDeserializer(BaseEnum.class, BaseEnumDeserializer.SERIALIZER_INSTANCE);
        simpleModule.setDeserializers(deserializers);
        return simpleModule;
    }

    /**
     * 大数值序列化及反序列化配置
     *
     * @return SimpleModule /
     * @since 2.12.1
     */
    private SimpleModule bigNumberModule() {
        SimpleModule bigNumberModule = new SimpleModule();
        switch (properties.getBigNumberSerializeMode()) {
            case FLEXIBLE -> {
                bigNumberModule.addSerializer(Long.class, BigNumberSerializer.SERIALIZER_INSTANCE);
                bigNumberModule.addSerializer(Long.TYPE, BigNumberSerializer.SERIALIZER_INSTANCE);
                bigNumberModule.addSerializer(BigInteger.class, BigNumberSerializer.SERIALIZER_INSTANCE);
            }
            case TO_STRING -> {
                bigNumberModule.addSerializer(Long.class, ToStringSerializer.instance);
                bigNumberModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
                bigNumberModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
            }
            default -> log.warn("[Vanta Starter] - Jackson 大数值序列化模式：NO_OPERATE，超过 JS 范围的数值会损失精度");
        }
        return bigNumberModule;
    }
}
