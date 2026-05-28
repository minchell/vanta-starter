package com.vanta.starter.elasticsearch.core;

/**
 * Elasticsearch 索引名策略。
 *
 * <p>业务可以通过替换该接口实现环境前缀、租户前缀、日期后缀等索引命名规则。</p>
 */
@FunctionalInterface
public interface IndexNameStrategy {

    /**
     * 将业务索引名解析为真实索引名。
     *
     * @param rawIndexName 业务传入的索引名
     * @return 实际访问 Elasticsearch 的索引名
     */
    String resolve(String rawIndexName);
}
