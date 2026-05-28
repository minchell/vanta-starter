package com.vanta.starter.data.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 组合枚举类型处理器。
 * <p>
 * 该处理器在 MyBatis 原生枚举处理器和 Vanta/MyBatis-Plus 枚举处理器之间做选择：
 * 对实现 Vanta {@code BaseEnum}、MyBatis-Plus {@code IEnum} 或标记 {@code @EnumValue} 的枚举使用自定义处理器，
 * 其他枚举退回默认枚举处理器。
 * </p>
 *
 * @param <E> 枚举类型。
 */
public class CompositeBaseEnumTypeHandler<E extends Enum<E>> implements TypeHandler<E> {

    /**
     * 枚举类型是否属于 MyBatis-Plus 枚举的缓存。
     */
    private static final Map<Class<?>, Boolean> MP_ENUM_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认枚举类型处理器。
     */
    private static Class<? extends TypeHandler> defaultEnumTypeHandler = EnumTypeHandler.class;

    /**
     * 实际委托的枚举类型处理器。
     */
    private final TypeHandler<E> delegate;

    /**
     * 创建组合枚举类型处理器。
     *
     * @param enumClassType 枚举类型。
     */
    public CompositeBaseEnumTypeHandler(Class<E> enumClassType) {
        if (enumClassType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        if (Boolean.TRUE.equals(CollectionUtils
                .computeIfAbsent(MP_ENUM_CACHE, enumClassType, MybatisBaseEnumTypeHandler::isMpEnums))) {
            delegate = new MybatisBaseEnumTypeHandler<>(enumClassType);
        } else {
            delegate = getInstance(enumClassType, defaultEnumTypeHandler);
        }
    }

    /**
     * 设置默认枚举类型处理器。
     *
     * @param defaultEnumTypeHandler 默认枚举类型处理器类型。
     */
    public static void setDefaultEnumTypeHandler(Class<? extends TypeHandler> defaultEnumTypeHandler) {
        if (defaultEnumTypeHandler != null && !MybatisBaseEnumTypeHandler.class
                .isAssignableFrom(defaultEnumTypeHandler)) {
            CompositeBaseEnumTypeHandler.defaultEnumTypeHandler = defaultEnumTypeHandler;
        }
    }

    /**
     * 设置 SQL 参数。
     *
     * @param ps        预编译语句。
     * @param i         参数索引。
     * @param parameter 枚举参数。
     * @param jdbcType  JDBC 类型。
     * @throws SQLException 设置参数失败时抛出。
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        delegate.setParameter(ps, i, parameter, jdbcType);
    }

    /**
     * 按列名读取结果。
     *
     * @param rs         结果集。
     * @param columnName 列名。
     * @return 枚举结果。
     * @throws SQLException 读取失败时抛出。
     */
    @Override
    public E getResult(ResultSet rs, String columnName) throws SQLException {
        return delegate.getResult(rs, columnName);
    }

    /**
     * 按列索引读取结果。
     *
     * @param rs          结果集。
     * @param columnIndex 列索引。
     * @return 枚举结果。
     * @throws SQLException 读取失败时抛出。
     */
    @Override
    public E getResult(ResultSet rs, int columnIndex) throws SQLException {
        return delegate.getResult(rs, columnIndex);
    }

    /**
     * 从存储过程输出参数读取结果。
     *
     * @param cs          CallableStatement。
     * @param columnIndex 列索引。
     * @return 枚举结果。
     * @throws SQLException 读取失败时抛出。
     */
    @Override
    public E getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return delegate.getResult(cs, columnIndex);
    }

    /**
     * 实例化指定类型处理器。
     *
     * @param javaTypeClass    Java 类型。
     * @param typeHandlerClass 类型处理器类型。
     * @param <T>              Java 类型泛型。
     * @return 类型处理器实例。
     */
    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
            try {
                Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
                return (TypeHandler<T>) c.newInstance(javaTypeClass);
            } catch (NoSuchMethodException ignored) {
                // ignored
            } catch (Exception e) {
                throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }
}
