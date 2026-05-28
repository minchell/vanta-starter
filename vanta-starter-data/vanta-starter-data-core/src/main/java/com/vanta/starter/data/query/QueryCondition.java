package com.vanta.starter.data.query;

/**
 * 查询条件标记接口。
 * <p>
 * 业务层只能传递领域查询对象或该标记接口的实现，不能传递 MyBatis-Plus Wrapper。
 * Repository 实现负责把查询条件转换为具体 SQL 条件。
 * </p>
 */
public interface QueryCondition {
}
