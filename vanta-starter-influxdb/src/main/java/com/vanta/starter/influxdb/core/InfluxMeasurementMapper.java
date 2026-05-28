package com.vanta.starter.influxdb.core;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.query.InfluxQLQueryResult;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * InfluxQL 查询结果映射工具。
 *
 * <p>该工具面向简单对象映射场景：读取 measurement 类上的 {@link Measurement}，
 * 再按字段上的 {@link Column} 从 InfluxQL record 中取值。</p>
 */
public final class InfluxMeasurementMapper {

    private InfluxMeasurementMapper() {
    }

    /**
     * 读取 measurement 类型上的 InfluxDB measurement 名称。
     *
     * @param clazz 带 {@link Measurement} 注解的类型
     * @return measurement 名称
     */
    public static <M> String measurementName(Class<M> clazz) {
        Measurement measurement = clazz.getAnnotation(Measurement.class);
        if (measurement == null) {
            throw new IllegalArgumentException("Class must declare @Measurement: " + clazz.getName());
        }
        return measurement.name();
    }

    /**
     * 将 InfluxQL 查询记录映射为业务 measurement 对象。
     *
     * <p>该方法会通过无参构造函数创建目标对象，并读取字段上的 {@link Column} 注解，
     * 将 InfluxDB 返回的列值转换为字段类型后写入对象。调用方应保证目标类存在无参构造函数，
     * 且需要映射的字段已经声明 {@link Column} 注解。</p>
     *
     * @param record InfluxQL 查询返回的单行记录
     * @param clazz  目标 measurement 类型
     * @param <T>    目标 measurement 类型
     * @return 映射后的目标对象
     */
    public static <T> T map(InfluxQLQueryResult.Series.Record record, Class<T> clazz) {
        try {
            T entity = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                if (column == null) {
                    continue;
                }
                Object value = record.getValueByKey(column.timestamp() ? "time" : columnName(column, field));
                if (value == null) {
                    continue;
                }
                field.setAccessible(true);
                field.set(entity, convert(value, field.getType(), column.timestamp()));
            }
            return entity;
        } catch (Exception ex) {
            throw new IllegalStateException("failed to map Influx record to " + clazz.getName(), ex);
        }
    }

    /**
     * 解析字段对应的 InfluxDB 列名。
     *
     * @param column 字段上的 {@link Column} 注解
     * @param field  Java 字段
     * @return InfluxDB 列名
     */
    private static String columnName(Column column, Field field) {
        return column.name() == null || column.name().isBlank() ? field.getName() : column.name();
    }

    /**
     * 转换 convert 的输入数据。
     * 该方法负责在底层客户端模型和业务可读模型之间做边界转换，避免调用方直接依赖底层细节。
     *
     * @param value      InfluxDB 返回的原始列值
     * @param targetType Java 字段目标类型
     * @param timestamp  当前列是否为时间戳列
     * @return 转换后的 Java 字段值
     */
    private static Object convert(Object value, Class<?> targetType, boolean timestamp) {

        if (targetType == Instant.class) {

            if (timestamp && value instanceof Number number) {
                return NanoClock.instant(number.longValue());
            }

            return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(String.valueOf(value)));
        }

        if (targetType == String.class) {
            return String.valueOf(value);
        }

        if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(String.valueOf(value));
        }

        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(String.valueOf(value));
        }

        if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(String.valueOf(value));
        }

        if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(String.valueOf(value));
        }

        return value;
    }

}
