package com.vanta.starter.data.routing;

@FunctionalInterface
public interface RepositoryShardDefinitionCustomizer {

    void customize(RepositoryShardDefinitionRegistry registry);
}
