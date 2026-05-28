package com.vanta.starter.data.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Repository 动态路由数据源。
 * <p>
 * 该数据源只读取 Repository 基础设施设置的 {@link RoutingDataSourceContext}，
 * 业务层不能直接设置数据源 key。
 * </p>
 */
public class RepositoryRoutingDataSource extends AbstractRoutingDataSource {

    /**
     * 决定当前数据源路由 key。
     *
     * @return 数据源路由 key。
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingDataSourceContext.current().orElse(RepositoryShardType.AUTO).name();
    }
}
