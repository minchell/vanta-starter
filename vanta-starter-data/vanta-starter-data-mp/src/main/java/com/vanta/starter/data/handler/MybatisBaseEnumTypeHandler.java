package com.vanta.starter.data.handler;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.vanta.starter.core.enums.BaseEnum;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Vanta/MyBatis-Plus 基础枚举类型处理器。
 * <p>
 * 该处理器支持三种枚举值来源：实现 {@link BaseEnum}、实现 MyBatis-Plus {@link IEnum}、
 * 或字段标记 {@link EnumValue}。数据库值会按枚举值字段反向解析为枚举实例。
 * </p>
 *
 * @param <E> 枚举类型。
 */
public class MybatisBaseEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    /**
     * 枚举类型到枚举值字段名的缓存。
     */
    private static final Map<String, String> TABLE_METHOD_OF_ENUM_TYPES = new ConcurrentHashMap<>();

    /**
     * MyBatis 反射工厂。
     */
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();

    /**
     * 当前处理器负责的枚举类型。
     */
    private final Class<E> enumClassType;

    /**
     * 枚举值属性类型。
     */
    private final Class<?> propertyType;

    /**
     * 枚举值读取调用器。
     */
    private final Invoker getInvoker;

    /**
     * 创建基础枚举类型处理器。
     *
     * @param enumClassType 枚举类型。
     */
    public MybatisBaseEnumTypeHandler(Class<E> enumClassType) {
        if (enumClassType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.enumClassType = enumClassType;
        MetaClass metaClass = MetaClass.forClass(enumClassType, REFLECTOR_FACTORY);
        String name = "value";
        if (!BaseEnum.class.isAssignableFrom(enumClassType) && !IEnum.class.isAssignableFrom(enumClassType)) {
            name = findEnumValueFieldName(this.enumClassType).orElseThrow(() -> new IllegalArgumentException(String
                    .format("Could not find @EnumValue in Class: %s.", this.enumClassType.getName())));
        }
        this.propertyType = ReflectionKit.resolvePrimitiveIfNecessary(metaClass.getGetterType(name));
        this.getInvoker = metaClass.getGetInvoker(name);
    }

    /**
     * 查找标记标记EnumValue字段
     *
     * @param clazz class
     * @return EnumValue字段
     */
    public static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        if (clazz != null && clazz.isEnum()) {
            String className = clazz.getName();
            return Optional.ofNullable(CollectionUtils.computeIfAbsent(TABLE_METHOD_OF_ENUM_TYPES, className, key -> {
                Optional<Field> fieldOptional = findEnumValueAnnotationField(clazz);
                return fieldOptional.map(Field::getName).orElse(null);
            }));
        }
        return Optional.empty();
    }

    /**
     * 查找标记 {@link EnumValue} 的字段。
     *
     * @param clazz 枚举类型。
     * @return 标记了 EnumValue 的字段。
     */
    private static Optional<Field> findEnumValueAnnotationField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(EnumValue.class))
                .findFirst();
    }

    /**
     * 判断是否为MP枚举处理
     *
     * @param clazz class
     * @return 是否为MP枚举处理
     */
    public static boolean isMpEnums(Class<?> clazz) {
        return clazz != null && clazz.isEnum() && (BaseEnum.class.isAssignableFrom(clazz) || IEnum.class
                .isAssignableFrom(clazz) || findEnumValueFieldName(clazz).isPresent());
    }

    /**
     * 设置非空枚举参数。
     *
     * @param ps        预编译语句。
     * @param i         参数索引。
     * @param parameter 枚举参数。
     * @param jdbcType  JDBC 类型。
     * @throws SQLException 设置参数失败时抛出。
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            ps.setObject(i, this.getValue(parameter));
        } else {
            // see r3589
            ps.setObject(i, this.getValue(parameter), jdbcType.TYPE_CODE);
        }
    }

    /**
     * 按列名读取可空枚举结果。
     *
     * @param rs         结果集。
     * @param columnName 列名。
     * @return 枚举结果；数据库值为空时返回 {@code null}。
     * @throws SQLException 读取失败时抛出。
     */
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName, this.propertyType);
        if (value == null || rs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }

    /**
     * 按列索引读取可空枚举结果。
     *
     * @param rs          结果集。
     * @param columnIndex 列索引。
     * @return 枚举结果；数据库值为空时返回 {@code null}。
     * @throws SQLException 读取失败时抛出。
     */
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex, this.propertyType);
        if (value == null || rs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }

    /**
     * 从存储过程输出参数读取可空枚举结果。
     *
     * @param cs          CallableStatement。
     * @param columnIndex 列索引。
     * @return 枚举结果；数据库值为空时返回 {@code null}。
     * @throws SQLException 读取失败时抛出。
     */
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex, this.propertyType);
        if (value == null || cs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }

    /**
     * 根据数据库值反向查找枚举实例。
     *
     * @param value 数据库字段值。
     * @return 匹配的枚举实例；未匹配时返回 {@code null}。
     */
    private E valueOf(Object value) {
        E[] es = this.enumClassType.getEnumConstants();
        return Arrays.stream(es).filter(e -> equalsValue(value, getValue(e))).findAny().orElse(null);
    }

    /**
     * 值比较
     *
     * @param sourceValue 数据库字段值
     * @param targetValue 当前枚举属性值
     * @return 是否匹配
     */
    private boolean equalsValue(Object sourceValue, Object targetValue) {
        String sValue = StringUtils.toStringTrim(sourceValue);
        String tValue = StringUtils.toStringTrim(targetValue);
        if (sourceValue instanceof Number && targetValue instanceof Number && new BigDecimal(sValue)
                .compareTo(new BigDecimal(tValue)) == 0) {
            return true;
        }
        return Objects.equals(sValue, tValue);
    }

    /**
     * 读取枚举实例上的枚举值。
     *
     * @param object 枚举实例。
     * @return 枚举值。
     */
    private Object getValue(Object object) {
        try {
            return this.getInvoker.invoke(object, new Object[0]);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe(e);
        }
    }
}
