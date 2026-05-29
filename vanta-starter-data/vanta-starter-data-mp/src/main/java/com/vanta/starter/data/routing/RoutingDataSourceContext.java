package com.vanta.starter.data.routing;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;

public final class RoutingDataSourceContext {

    private static final ThreadLocal<Deque<String>> SHARD_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private RoutingDataSourceContext() {
    }

    public static void push(String shardKey, RepositoryShardDefinitionRegistry registry) {
        push(shardKey, registry, Set.of());
    }

    public static void push(String shardKey, RepositoryShardDefinitionRegistry registry, Set<String> lookupKeys) {
        String nextKey = resolve(shardKey, registry, lookupKeys);
        RepositoryShardDefinition next = registry.require(nextKey);
        RepositoryShardDefinition current = current().map(registry::require).orElse(null);

        TransactionRoutingGuard.assertCanRouteTo(next, current, TransactionSynchronizationManager.isActualTransactionActive());
        SHARD_STACK.get().push(next.key());
    }

    private static String resolve(String shardKey, RepositoryShardDefinitionRegistry registry, Set<String> lookupKeys) {
        String requestedKey = shardKey == null ? RepositoryShardKeys.AUTO : shardKey;
        RepositoryShardDefinition requested = registry.require(requestedKey);

        if (RepositoryShardKeys.AUTO.equals(requested.key())) {
            requested = registry.require(TransactionSynchronizationManager.isActualTransactionActive()
                    ? RepositoryShardKeys.MASTER
                    : RepositoryShardKeys.READ);
        }

        if (TransactionSynchronizationManager.isActualTransactionActive() && requested.fallbackKey() != null) {
            requested = registry.require(requested.fallbackKey());
        }

        if (!lookupKeys.isEmpty() && !lookupKeys.contains(requested.key())) {
            String fallbackKey = requested.fallbackKey();
            if (fallbackKey != null && lookupKeys.contains(fallbackKey)) {
                return fallbackKey;
            }
            throw new IllegalStateException("Repository shard key has no DataSource lookup key: " + requested.key());
        }

        return requested.key();
    }

    public static void pop() {
        Deque<String> stack = SHARD_STACK.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
        if (stack.isEmpty()) {
            SHARD_STACK.remove();
        }
    }

    public static Optional<String> current() {
        Deque<String> stack = SHARD_STACK.get();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack.peek());
    }

    public static void clear() {
        SHARD_STACK.remove();
    }
}
