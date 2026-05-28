package com.vanta.starter.core.util;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * 三元组值对象。
 * <p>
 * 该类用于在不引入额外业务 DTO 的情况下临时承载三个相关值。
 * 它是不可变对象，创建后 {@code first}、{@code second}、{@code three} 三个值不会再变化。
 * </p>
 *
 * @param <S> 第一个元素类型。
 * @param <T> 第二个元素类型。
 * @param <V> 第三个元素类型。
 */
public final class Tuple3<S, T, V> {

    /**
     * 第一个元素。
     */
    private final S first;

    /**
     * 第二个元素。
     */
    private final T second;

    /**
     * 第三个元素。
     */
    private final V three;

    /**
     * 创建三元组实例。
     *
     * @param first  第一个元素。
     * @param second 第二个元素。
     * @param three  第三个元素。
     */
    private Tuple3(S first, T second, V three) {
        this.first = first;
        this.second = second;
        this.three = three;
    }

    /**
     * 创建新的三元组。
     *
     * @param first  第一个元素。
     * @param second 第二个元素。
     * @param three  第三个元素。
     * @param <S>    第一个元素类型。
     * @param <T>    第二个元素类型。
     * @param <V>    第三个元素类型。
     * @return 包含三个元素的不可变三元组。
     */
    public static <S, T, V> Tuple3<S, T, V> of(S first, T second, V three) {
        return new Tuple3<>(first, second, three);
    }

    /**
     * 判断当前三元组是否与另一个对象相等。
     *
     * @param o 待比较对象。
     * @return 三个元素都相等时返回 {@code true}。
     */
    @Override
    public boolean equals(@Nullable Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Tuple3<?, ?, ?> tuple3)) {
            return false;
        }

        if (!ObjectUtils.nullSafeEquals(first, tuple3.first)) {
            return false;
        }

        if (!ObjectUtils.nullSafeEquals(second, tuple3.second)) {
            return false;
        }

        return ObjectUtils.nullSafeEquals(three, tuple3.three);
    }

    /**
     * 计算三元组哈希值。
     *
     * @return 基于三个元素计算得到的哈希值。
     */
    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(first);
        result = 15 * result + ObjectUtils.nullSafeHashCode(second);
        result = 16 * result + ObjectUtils.nullSafeHashCode(three);
        return result;
    }

    /**
     * 返回三元组的字符串表达。
     *
     * @return 以 {@code first->second->three} 形式拼接的字符串。
     */
    @Override
    public String toString() {
        return String.format("%s->%s->%s", this.first, this.second, this.three);
    }

    /**
     * 获取第一个元素。
     *
     * @return 第一个元素
     */
    public S getFirst() {
        return first;
    }

    /**
     * 获取第二个元素。
     *
     * @return 第二个元素
     */
    public T getSecond() {
        return second;
    }

    /**
     * 获取第三个元素。
     *
     * @return 第三个元素
     */
    public V getThree() {
        return three;
    }
}
