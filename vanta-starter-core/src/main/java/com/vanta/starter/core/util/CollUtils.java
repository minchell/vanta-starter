package com.vanta.starter.core.util;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合转换工具类。
 * <p>
 * 该工具类聚合常用集合映射逻辑，主要用于把实体、枚举或配置对象列表提取为字段列表或去重集合。
 * 所有方法只处理传入集合，不会修改外部服务状态；是否过滤 {@code null} 由调用方通过参数控制。
 * </p>
 */
public class CollUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private CollUtils() {
    }

    /**
     * 通过 func 自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的列表返回<br>
     * 例如：提供一个 Bean 列表，通过 Function 接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @return 抽取后的新列表（默认去除 null 值）
     * @see CollUtil#map(Iterable, Function, boolean)
     */
    public static <T, R> List<R> mapToList(Collection<T> collection, Function<? super T, ? extends R> func) {
        return mapToList(collection, func, true);
    }

    /**
     * 通过 func 自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的列表返回<br>
     * 例如：提供一个 Bean 列表，通过 Function 接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @param ignoreNull 是否忽略空值，这里的空值包括函数处理前和处理后的 null 值
     * @return 抽取后的新列表
     * @see CollUtil#map(Iterable, Function, boolean)
     */
    public static <T, R> List<R> mapToList(Collection<T> collection,
                                           Function<? super T, ? extends R> func,
                                           boolean ignoreNull) {
        if (CollUtil.isEmpty(collection)) {
            return new ArrayList<>(0);
        }
        Stream<T> stream = collection.stream();
        if (ignoreNull) {
            return stream.filter(Objects::nonNull).map(func).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return stream.map(func).collect(Collectors.toList());
    }

    /**
     * 通过 func 自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的集合返回<br>
     * 例如：提供一个 Bean 集合，通过 Function 接口实现获取某个字段值，返回这个字段值组成的新集合
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @return 抽取后的新集合（默认去除 null 值）
     * @see CollUtil#map(Iterable, Function, boolean)
     */
    public static <T, R> Set<R> mapToSet(Collection<T> collection, Function<? super T, ? extends R> func) {
        return mapToSet(collection, func, true);
    }

    /**
     * 通过 func 自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的集合返回<br>
     * 例如：提供一个 Bean 集合，通过 Function 接口实现获取某个字段值，返回这个字段值组成的新集合
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @param ignoreNull 是否忽略空值，这里的空值包括函数处理前和处理后的 null 值
     * @return 抽取后的新集合
     * @see CollUtil#map(Iterable, Function, boolean)
     */
    public static <T, R> Set<R> mapToSet(Collection<T> collection,
                                         Function<? super T, ? extends R> func,
                                         boolean ignoreNull) {
        if (CollUtil.isEmpty(collection)) {
            return new HashSet<>(0);
        }
        Stream<T> stream = collection.stream();
        if (ignoreNull) {
            return stream.filter(Objects::nonNull).map(func).filter(Objects::nonNull).collect(Collectors.toSet());
        }
        return stream.map(func).collect(Collectors.toSet());
    }
}
