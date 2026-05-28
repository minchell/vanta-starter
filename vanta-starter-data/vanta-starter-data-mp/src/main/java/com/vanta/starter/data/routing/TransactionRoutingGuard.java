package com.vanta.starter.data.routing;

/**
 * 事务期间的数据源切换保护器。
 */
public final class TransactionRoutingGuard {

    /**
     * 私有构造，禁止实例化。
     */
    private TransactionRoutingGuard() {
    }

    /**
     * 校验事务期间的数据源路由是否安全。
     *
     * @param next 目标数据源。
     * @param current 当前数据源。
     * @param transactionActive 当前线程是否存在活动事务。
     */
    public static void assertCanRouteTo(RepositoryShardType next, RepositoryShardType current, boolean transactionActive) {
        if (!transactionActive || current == null || next == null || current == next) {
            return;
        }
        throw new IllegalStateException("事务已开始，禁止在 " + current + " 与 " + next + " 数据源之间切换");
    }
}
