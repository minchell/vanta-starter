package com.vanta.starter.data.enums;

/**
 * 查询条件逻辑关系枚举。
 * <p>
 * 当一个查询字段映射到多个数据库列时，该枚举用于描述多个列条件之间使用 AND 还是 OR 连接。
 * </p>
 */
public enum LogicalRelation {

    /**
     * 并且关系
     */
    AND,

    /**
     * 或者关系
     */
    OR
}
