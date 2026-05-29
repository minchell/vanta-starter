package com.vanta.starter.data.routing;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class RepositoryShardDefinitionRegistry {

    private final Map<String, RepositoryShardDefinition> definitions = new LinkedHashMap<>();

    public static RepositoryShardDefinitionRegistry createDefault() {
        RepositoryShardDefinitionRegistry registry = new RepositoryShardDefinitionRegistry();
        registry.register(RepositoryShardDefinition.strategy(
                RepositoryShardKeys.AUTO,
                RepositoryShardDefinition.DEFAULT_GROUP,
                RepositoryShardKeys.MASTER));
        registry.register(RepositoryShardDefinition.transactional(
                RepositoryShardKeys.MASTER,
                RepositoryShardDefinition.DEFAULT_GROUP));
        registry.register(RepositoryShardDefinition.readOnly(
                RepositoryShardKeys.READ,
                RepositoryShardDefinition.DEFAULT_GROUP,
                RepositoryShardKeys.MASTER));
        return registry;
    }

    public void register(RepositoryShardDefinition definition) {
        RepositoryShardDefinition previous = definitions.putIfAbsent(definition.key(), definition);
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate repository shard key: " + definition.key());
        }
    }

    public void validateDefinitions() {
        for (RepositoryShardDefinition definition : definitions.values()) {
            String fallbackKey = definition.fallbackKey();
            if (fallbackKey != null && !definitions.containsKey(fallbackKey)) {
                throw new IllegalStateException("Repository shard key " + definition.key()
                        + " fallback shard key is not registered: " + fallbackKey);
            }
        }
    }

    public boolean contains(String key) {
        return definitions.containsKey(RepositoryShardDefinition.normalizeKey(key));
    }

    public RepositoryShardDefinition require(String key) {
        String normalized = RepositoryShardDefinition.normalizeKey(key);
        RepositoryShardDefinition definition = definitions.get(normalized);
        if (definition == null) {
            throw new IllegalArgumentException("Unregistered repository shard key: " + normalized);
        }
        return definition;
    }

    public Optional<RepositoryShardDefinition> find(String key) {
        return Optional.ofNullable(definitions.get(RepositoryShardDefinition.normalizeKey(key)));
    }

    public Collection<RepositoryShardDefinition> definitions() {
        return definitions.values();
    }
}
