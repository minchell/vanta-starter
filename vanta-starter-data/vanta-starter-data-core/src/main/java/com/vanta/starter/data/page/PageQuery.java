package com.vanta.starter.data.page;

import java.io.Serializable;

/**
 * 统一分页查询对象。
 * <p>
 * 该对象用于业务层和 Repository 接口之间传递分页意图，避免业务层直接依赖
 * MyBatis-Plus 的 {@code Page} 类型。Repository 实现可以在内部把该对象转换为
 * 具体持久化框架需要的分页对象，但这种转换不得向业务层泄漏。
 * </p>
 */
public class PageQuery implements Serializable {

    /**
     * 默认当前页。
     */
    private static final long DEFAULT_CURRENT = 1L;

    /**
     * 默认每页条数。
     */
    private static final long DEFAULT_SIZE = 10L;

    /**
     * 最大每页条数。
     */
    private static final long MAX_SIZE = 500L;

    /**
     * 当前页，从 1 开始。
     */
    private long current = DEFAULT_CURRENT;

    /**
     * 每页条数。
     */
    private long size = DEFAULT_SIZE;

    /**
     * 创建默认分页查询。
     */
    public PageQuery() {
    }

    /**
     * 创建指定页码和条数的分页查询。
     *
     * @param current 当前页。
     * @param size 每页条数。
     */
    public PageQuery(long current, long size) {
        this.current = normalizeCurrent(current);
        this.size = normalizeSize(size);
    }

    /**
     * 创建分页查询对象。
     *
     * @param current 当前页。
     * @param size 每页条数。
     * @return 分页查询对象。
     */
    public static PageQuery of(long current, long size) {
        return new PageQuery(current, size);
    }

    /**
     * 获取当前页。
     *
     * @return 当前页。
     */
    public long getCurrent() {
        return current;
    }

    /**
     * 设置当前页。
     *
     * @param current 当前页。
     */
    public void setCurrent(long current) {
        this.current = normalizeCurrent(current);
    }

    /**
     * 获取每页条数。
     *
     * @return 每页条数。
     */
    public long getSize() {
        return size;
    }

    /**
     * 设置每页条数。
     *
     * @param size 每页条数。
     */
    public void setSize(long size) {
        this.size = normalizeSize(size);
    }

    /**
     * 规整当前页，防止外部传入 0 或负数。
     *
     * @param current 原始当前页。
     * @return 可用当前页。
     */
    private static long normalizeCurrent(long current) {
        return current <= 0 ? DEFAULT_CURRENT : current;
    }

    /**
     * 规整每页条数，防止过大分页拖垮数据库。
     *
     * @param size 原始每页条数。
     * @return 可用每页条数。
     */
    private static long normalizeSize(long size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
