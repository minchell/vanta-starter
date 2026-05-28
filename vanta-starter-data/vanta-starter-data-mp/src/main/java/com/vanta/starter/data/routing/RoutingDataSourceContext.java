package com.vanta.starter.data.routing;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * 数据源路由上下文。
 */
public final class RoutingDataSourceContext {

    /**
     * 当前线程的数据源路由栈。
     */
    private static final ThreadLocal<Deque<RepositoryShardType>> SHARD_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 私有构造，禁止实例化。
     */
    private RoutingDataSourceContext() {
    }

    /**
     * 压入数据源定位。
     *
     * @param shardType 数据源定位类型。
     */
    public static void push(RepositoryShardType shardType) {
        RepositoryShardType next = shardType == null ? RepositoryShardType.AUTO : shardType;

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            next = RepositoryShardType.MASTER;
        }

        TransactionRoutingGuard.assertCanRouteTo(next, current().orElse(null), TransactionSynchronizationManager.isActualTransactionActive());
        SHARD_STACK.get().push(next);
    }

    /**
     * 弹出当前数据源定位。
     */
    public static void pop() {
        Deque<RepositoryShardType> stack = SHARD_STACK.get();

        if (!stack.isEmpty()) {
            stack.pop();
        }

        if (stack.isEmpty()) {
            SHARD_STACK.remove();
        }
    }

    /**
     * 获取当前数据源定位。
     *
     * @return 当前数据源定位。
     */
    public static Optional<RepositoryShardType> current() {
        Deque<RepositoryShardType> stack = SHARD_STACK.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack.peek());
    }

    /**
     * 清理线程上下文。
     */
    public static void clear() {
        SHARD_STACK.remove();
    }
}
