package com.vanta.starter.data.routing;

import java.util.Objects;

public final class RepositoryShardDefinition {

    public static final String DEFAULT_GROUP = "DEFAULT";

    private final String key;

    private final String group;

    private final boolean readOnly;

    private final boolean transactional;

    private final String fallbackKey;

    private RepositoryShardDefinition(String key, String group, boolean readOnly, boolean transactional, String fallbackKey) {
        this.key = normalizeKey(key);
        this.group = normalizeGroup(group);
        this.readOnly = readOnly;
        this.transactional = transactional;
        this.fallbackKey = normalizeOptionalKey(fallbackKey);
    }

    public static RepositoryShardDefinition strategy(String key, String group, String fallbackKey) {
        return new RepositoryShardDefinition(key, group, false, false, fallbackKey);
    }

    public static RepositoryShardDefinition transactional(String key, String group) {
        return new RepositoryShardDefinition(key, group, false, true, null);
    }

    public static RepositoryShardDefinition readOnly(String key, String group) {
        return new RepositoryShardDefinition(key, group, true, false, null);
    }

    public static RepositoryShardDefinition readOnly(String key, String group, String fallbackKey) {
        return new RepositoryShardDefinition(key, group, true, false, fallbackKey);
    }

    public String key() {
        return key;
    }

    public String group() {
        return group;
    }

    public boolean readOnly() {
        return readOnly;
    }

    public boolean transactional() {
        return transactional;
    }

    public String fallbackKey() {
        return fallbackKey;
    }

    public boolean sameGroup(RepositoryShardDefinition other) {
        return other != null && group.equals(other.group);
    }

    static String normalizeKey(String key) {
        String normalized = Objects.requireNonNull(key, "Repository shard key must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Repository shard key must not be blank");
        }
        return normalized;
    }

    private static String normalizeGroup(String group) {
        String normalized = Objects.requireNonNull(group, "Repository shard group must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Repository shard group must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptionalKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        return normalizeKey(key);
    }
}
