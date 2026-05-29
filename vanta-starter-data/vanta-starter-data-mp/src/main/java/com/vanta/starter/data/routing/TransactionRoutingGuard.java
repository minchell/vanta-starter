package com.vanta.starter.data.routing;

public final class TransactionRoutingGuard {

    private TransactionRoutingGuard() {
    }

    public static void assertCanRouteTo(RepositoryShardDefinition next,
                                        RepositoryShardDefinition current,
                                        boolean transactionActive) {
        if (!transactionActive || current == null || next == null || current.key().equals(next.key())) {
            return;
        }
        if (current.sameGroup(next)) {
            return;
        }
        throw new IllegalStateException("Transaction already started, cannot switch repository shard from "
                + current.key() + " to " + next.key());
    }
}
