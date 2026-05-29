package com.vanta.starter.data.routing;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class RepositoryShardValidator implements SmartInitializingSingleton {

    private final ApplicationContext applicationContext;

    private final RepositoryShardDefinitionRegistry registry;

    private final Set<String> lookupKeys;

    public RepositoryShardValidator(ApplicationContext applicationContext,
                                    RepositoryShardDefinitionRegistry registry,
                                    Set<String> lookupKeys) {
        this.applicationContext = applicationContext;
        this.registry = registry;
        this.lookupKeys = lookupKeys == null ? Set.of() : lookupKeys;
    }

    @Override
    public void afterSingletonsInstantiated() {
        registry.validateDefinitions();
        for (String shardKey : scanRepositoryShardKeys()) {
            registry.require(shardKey);
        }
        validateLookupKeys();
    }

    private void validateLookupKeys() {
        if (lookupKeys.isEmpty()) {
            return;
        }
        for (RepositoryShardDefinition definition : registry.definitions()) {
            if (RepositoryShardKeys.AUTO.equals(definition.key())) {
                continue;
            }
            if (!lookupKeys.contains(definition.key())) {
                String fallbackKey = definition.fallbackKey();
                if (fallbackKey == null || !lookupKeys.contains(fallbackKey)) {
                    throw new IllegalStateException("Repository shard key has no DataSource lookup key: " + definition.key());
                }
            }
        }
    }

    private Set<String> scanRepositoryShardKeys() {
        Set<String> shardKeys = new LinkedHashSet<>();
        Map<String, Object> repositories = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Repository.class);
        for (Object bean : repositories.values()) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            RepositoryShard classShard = AnnotatedElementUtils.findMergedAnnotation(targetClass, RepositoryShard.class);
            if (classShard != null) {
                shardKeys.add(classShard.value());
            }
            for (Method method : targetClass.getDeclaredMethods()) {
                RepositoryShard methodShard = AnnotatedElementUtils.findMergedAnnotation(method, RepositoryShard.class);
                if (methodShard != null) {
                    shardKeys.add(methodShard.value());
                }
            }
        }
        return shardKeys;
    }
}
