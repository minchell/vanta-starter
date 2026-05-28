package com.vanta.starter.data.routing;

/**
 * Repository 数据源定位类型。
 * <p>
 * 该枚举只描述基础设施层的数据源路由意图，不属于业务语义。
 * </p>
 */
public enum RepositoryShardType {

    /**
     * 自动路由，由基础设施根据事务和操作类型决定。
     */
    AUTO,

    /**
     * 主库路由，适合写入和需要强一致读的场景。
     */
    MASTER,

    /**
     * 只读路由，适合非事务读场景。
     */
    READ
}
