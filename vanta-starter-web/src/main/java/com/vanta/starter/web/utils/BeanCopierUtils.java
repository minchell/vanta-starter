package com.vanta.starter.web.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * Bean 属性复制工具类。
 * <p>
 * 该工具类基于 CGLIB {@link BeanCopier} 复制同名同类型属性，并缓存源类型到目标类型的复制器。
 * 它适合 DTO、VO、DO 之间的轻量属性复制；复杂映射、字段改名或类型转换应使用专门映射逻辑。
 * </p>
 */
public class BeanCopierUtils {

    /**
     * BeanCopier 缓存。
     * <p>
     * key 由源类型全限定名和目标类型全限定名拼接而成，避免重复创建 CGLIB 复制器。
     * </p>
     */
    private static final ConcurrentMap<String, BeanCopier> BEAN_COPIER_CACHE = Maps.newConcurrentMap();

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private BeanCopierUtils() {

    }

    /**
     * 复制单个对象到指定目标类型。
     *
     * @param source 源对象。
     * @param clz    目标类型。
     * @param <S>    源对象类型。
     * @param <T>    目标对象类型。
     * @return 目标对象；源对象或目标类型为空时返回 {@code null}。
     */
    public static <S, T> T copy(S source, Class<T> clz) {
        return copy(source, clz, null);
    }

    /**
     * 复制单个对象到指定目标类型，并允许复制后回调补充字段。
     *
     * @param source   源对象。
     * @param clz      目标类型。
     * @param consumer 复制后回调，允许调用方补充复杂字段。
     * @param <S>      源对象类型。
     * @param <T>      目标对象类型。
     * @return 目标对象；源对象或目标类型为空时返回 {@code null}。
     */
    public static <S, T> T copy(S source, Class<T> clz, BiConsumer<S, T> consumer) {

        if (source == null || clz == null) return null;

        T target = BeanUtils.instantiateClass(clz);

        var copier = getBeanCopier(source.getClass(), clz);

        copier.copy(source, target, null);

        if (null != consumer) consumer.accept(source, target);

        return target;


    }


    /**
     * 批量复制对象列表到指定目标类型，并允许复制后回调补充字段。
     *
     * @param source   源对象列表。
     * @param clz      目标类型。
     * @param consumer 复制后回调，允许调用方补充复杂字段。
     * @param <S>      源对象类型。
     * @param <T>      目标对象类型。
     * @return 目标对象列表；源列表为空或目标类型为空时返回空列表。
     */
    public static <S, T> List<T> copyArray(List<S> source, Class<T> clz, BiConsumer<S, T> consumer) {

        if (CollectionUtils.isEmpty(source) || clz == null) return Collections.emptyList();

        var copier = getBeanCopier(source.get(0).getClass(), clz);

        var targets = new ArrayList<T>(source.size());

        for (var src : source) {

            T target = BeanUtils.instantiateClass(clz);
            copier.copy(src, target, null);
            targets.add(target);

            if (null != consumer) consumer.accept(src, target);

        }

        return targets;

    }

    /**
     * 批量复制对象列表到指定目标类型。
     *
     * @param source 源对象列表。
     * @param clz    目标类型。
     * @param <S>    源对象类型。
     * @param <T>    目标对象类型。
     * @return 目标对象列表；源列表为空或目标类型为空时返回空列表。
     */
    public static <S, T> List<T> copyArray(List<S> source, Class<T> clz) {

        return copyArray(source, clz, null);
    }


    /**
     * 获取源类型到目标类型的 BeanCopier。
     *
     * @param sourceClass 源对象类型。
     * @param targetClass 目标对象类型。
     * @param <S>         源对象类型。
     * @param <T>         目标对象类型。
     * @return 缓存中的 BeanCopier，不存在时创建并缓存。
     */
    private static <S, T> BeanCopier getBeanCopier(Class<S> sourceClass, Class<T> targetClass) {

        var key = Joiner.on("@").join(sourceClass.getName(), targetClass.getName());

        BeanCopier beanCopier = BEAN_COPIER_CACHE.get(key);

        if (beanCopier == null) {

            BEAN_COPIER_CACHE.putIfAbsent(key, BeanCopier.create(sourceClass, targetClass, false));

        }
        return BEAN_COPIER_CACHE.get(key);
    }
}
