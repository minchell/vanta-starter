package com.vanta.starter.core.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.TypeUtil;

import java.lang.reflect.Type;

/**
 * Java 类型解析工具类。
 * <p>
 * 该工具类封装 Hutool 的类型解析能力，用于在 starter 或业务代码中读取父类、接口上已经确定的泛型参数。
 * 工具类不保存状态，也不会触发类实例化，适合在自动配置和通用组件中安全调用。
 * </p>
 */
public class ClassUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private ClassUtils() {
    }

    /**
     * 获得给定类的所有泛型参数
     *
     * @param clazz 被检查的类，必须是已经确定泛型类型的类
     * @return {@link Class}[]
     */
    public static Class<?>[] getTypeArguments(Class<?> clazz) {
        final Type[] typeArguments = TypeUtil.getTypeArguments(clazz);
        if (ArrayUtil.isEmpty(typeArguments)) {
            return new Class[0];
        }
        final Class<?>[] classes = new Class<?>[typeArguments.length];
        for (int i = 0; i < typeArguments.length; i++) {
            classes[i] = TypeUtil.getClass(typeArguments[i]);
        }
        return classes;
    }
}
