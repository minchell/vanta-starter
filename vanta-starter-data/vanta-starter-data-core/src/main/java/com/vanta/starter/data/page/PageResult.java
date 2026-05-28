package com.vanta.starter.data.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 统一分页结果对象。
 * <p>
 * 该对象用于 Repository 向业务层返回分页结果，避免业务层接触 MyBatis-Plus
 * 的 {@code IPage} 或 {@code Page}。分页元数据只保留业务需要的记录、总数、页码和页大小。
 * </p>
 *
 * @param <T> 记录类型。
 */
public class PageResult<T> implements Serializable {

    /**
     * 当前页记录。
     */
    private List<T> records = Collections.emptyList();

    /**
     * 总记录数。
     */
    private long total;

    /**
     * 当前页。
     */
    private long current;

    /**
     * 每页条数。
     */
    private long size;

    /**
     * 创建分页结果。
     */
    public PageResult() {
    }

    /**
     * 创建分页结果。
     *
     * @param records 当前页记录。
     * @param total   总记录数。
     * @param current 当前页。
     * @param size    每页条数。
     */
    public PageResult(List<T> records, long total, long current, long size) {
        this.records = records == null ? Collections.emptyList() : List.copyOf(records);
        this.total = total;
        this.current = current;
        this.size = size;
    }

    /**
     * 创建分页结果。
     *
     * @param records 当前页记录。
     * @param total   总记录数。
     * @param current 当前页。
     * @param size    每页条数。
     * @param <T>     记录类型。
     * @return 分页结果。
     */
    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        return new PageResult<>(records, total, current, size);
    }

    /**
     * 创建空分页结果。
     *
     * @param query 分页查询。
     * @param <T>   记录类型。
     * @return 空分页结果。
     */
    public static <T> PageResult<T> empty(PageQuery query) {
        PageQuery safeQuery = query == null ? new PageQuery() : query;
        return new PageResult<>(Collections.emptyList(), 0L, safeQuery.getCurrent(), safeQuery.getSize());
    }

    /**
     * 获取当前页记录。
     *
     * @return 当前页记录。
     */
    public List<T> getRecords() {
        return records;
    }

    /**
     * 设置当前页记录。
     *
     * @param records 当前页记录。
     */
    public void setRecords(List<T> records) {
        this.records = records == null ? Collections.emptyList() : List.copyOf(records);
    }

    /**
     * 获取总记录数。
     *
     * @return 总记录数。
     */
    public long getTotal() {
        return total;
    }

    /**
     * 设置总记录数。
     *
     * @param total 总记录数。
     */
    public void setTotal(long total) {
        this.total = total;
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
        this.current = current;
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
        this.size = size;
    }
}
