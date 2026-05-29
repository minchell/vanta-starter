package com.vanta.starter.data.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class RepositoryRoutingDataSource extends AbstractRoutingDataSource {

    private final Set<String> lookupKeys = new LinkedHashSet<>();

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);
        lookupKeys.clear();
        targetDataSources.keySet().forEach(key -> lookupKeys.add(String.valueOf(key)));
    }

    public Set<String> lookupKeys() {
        return Collections.unmodifiableSet(lookupKeys);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingDataSourceContext.current().orElse(RepositoryShardKeys.MASTER);
    }
}
