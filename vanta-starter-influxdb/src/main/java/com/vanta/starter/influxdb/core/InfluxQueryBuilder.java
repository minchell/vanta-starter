package com.vanta.starter.influxdb.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InfluxQL 查询构造器。
 *
 * <p>该构造器只覆盖常见查询场景，并对字符串值做基础转义。
 * 如果业务需要复杂 Flux/InfluxQL，建议直接写明确查询语句。</p>
 *
 * @param <M> measurement 类型
 */
public class InfluxQueryBuilder<M> {

    /**
     * SELECT 子句中的列名集合。
     * <p>为空时生成 {@code SELECT *}。</p>
     */
    private final List<String> selectColumns = new ArrayList<>();
    /**
     * WHERE 子句条件集合。
     * <p>每次调用 eq、gt、le 等方法都会追加一个 AND 条件。</p>
     */
    private final List<String> conditions = new ArrayList<>();
    /**
     * ORDER BY 子句集合。
     */
    private final List<String> orderBys = new ArrayList<>();
    /**
     * 查询目标 measurement 类型。
     * <p>通过 {@link InfluxMeasurementMapper#measurementName(Class)} 解析真实 measurement 名称。</p>
     */
    private Class<M> entityClass;
    /**
     * LIMIT 子句文本。
     * <p>未调用 limit 时保持空字符串。</p>
     */
    private String limitClause = "";

    /**
     * 指定查询的 measurement 类型。
     *
     * @param entityClass 带 {@code @Measurement} 注解的实体类型
     * @return 当前构造器
     */
    public InfluxQueryBuilder<M> from(Class<M> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    /**
     * 指定 SELECT 子句列名。
     *
     * @param columns 需要查询的列名
     * @return 当前构造器
     */
    public InfluxQueryBuilder<M> select(String... columns) {
        this.selectColumns.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * 添加等值查询条件。
     *
     * @param column 列名
     * @param value  条件值
     * @return 当前构造器
     */
    public InfluxQueryBuilder<M> eq(String column, Object value) {
        conditions.add(wrapColumn(column) + " = " + formatValue(value));
        return this;
    }

    /**
     * 添加大于查询条件。
     *
     * @param column 列名
     * @param value  条件值
     * @return 当前构造器
     */
    public InfluxQueryBuilder<M> gt(String column, Object value) {
        conditions.add(wrapColumn(column) + " > " + formatValue(value));
        return this;
    }

    /**
     * 添加小于等于查询条件。
     *
     * @param column 列名
     * @param value  条件值
     * @return 当前构造器
     */
    public InfluxQueryBuilder<M> le(String column, Object value) {
        conditions.add(wrapColumn(column) + " <= " + formatValue(value));
        return this;
    }

    /**
     * 添加排序字段。
     *
     * @param column    列名
     * @param direction 排序方向，例如 ASC 或 DESC
     * @return 当前构造器
     */
    public InfluxQueryBuilder<M> orderBy(String column, String direction) {
        orderBys.add(wrapColumn(column) + " " + direction.toUpperCase());
        return this;
    }

    /**
     * 添加 LIMIT 子句。
     *
     * @param limit 限制返回行数，小于 1 时自动提升为 1
     * @return 当前构造器
     */
    public InfluxQueryBuilder<M> limit(int limit) {
        this.limitClause = " LIMIT " + Math.max(1, limit);
        return this;
    }

    /**
     * 构建 InfluxQL 查询语句。
     *
     * @return 可直接交给 InfluxQLQueryApi 执行的查询语句
     */
    public String build() {
        if (entityClass == null) {
            throw new IllegalStateException("entityClass must be configured by from()");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(selectColumns.isEmpty() ? "*" : String.join(", ", selectColumns));
        sql.append(" FROM ").append(InfluxMeasurementMapper.measurementName(entityClass));
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        if (!orderBys.isEmpty()) {
            sql.append(" ORDER BY ").append(String.join(", ", orderBys));
        }
        sql.append(limitClause);
        return sql.toString();
    }

    /**
     * 转义列名。
     *
     * @param column 原始列名
     * @return 转义后的列名
     */
    private String wrapColumn(String column) {
        return Arrays.stream(column.split("\\."))
                .map(part -> part.replace("`", "``"))
                .collect(Collectors.joining("."));
    }

    /**
     * 格式化查询条件值。
     *
     * @param value 原始条件值
     * @return InfluxQL 可识别的值文本
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean bool) {
            return bool ? "1" : "0";
        }
        return "'" + value.toString().replace("\\", "\\\\").replace("'", "''") + "'";
    }
}
