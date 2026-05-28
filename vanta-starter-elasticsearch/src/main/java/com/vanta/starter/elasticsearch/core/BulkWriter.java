package com.vanta.starter.elasticsearch.core;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * Elasticsearch 批量写入工具。
 *
 * <p>批量写入是高吞吐场景常用能力，但失败处理通常和业务强相关。
 * 本工具只负责构造 bulk request 并返回原生响应，由业务决定如何处理失败项。</p>
 */
public class BulkWriter {

    /**
     * Elastic Java API Client。
     * <p>批量写入请求通过该客户端提交。</p>
     */
    private final ElasticsearchClient client;
    /**
     * 索引名策略。
     * <p>用于把业务索引名转换为真实索引名，保证批量写入与模板查询使用同一套命名规则。</p>
     */
    private final IndexNameStrategy indexNameStrategy;

    /**
     * 创建 Elasticsearch 批量写入工具。
     *
     * @param client            Elastic Java API Client
     * @param indexNameStrategy 索引名策略
     */
    public BulkWriter(ElasticsearchClient client, IndexNameStrategy indexNameStrategy) {
        this.client = client;
        this.indexNameStrategy = indexNameStrategy;
    }

    /**
     * 批量写入文档。
     *
     * @param index      业务索引名
     * @param documents  文档列表
     * @param idResolver 文档 ID 解析器；返回空时由 Elasticsearch 自动生成 ID
     * @return Elastic 原生 bulk 响应
     */
    public <T> BulkResponse indexMany(String index, List<T> documents, Function<T, String> idResolver) throws IOException {
        BulkRequest.Builder builder = new BulkRequest.Builder();
        String resolvedIndex = indexNameStrategy.resolve(index);
        for (T document : documents) {
            builder.operations(operation -> operation.index(indexOperation -> {
                indexOperation.index(resolvedIndex).document(document);
                String id = idResolver == null ? null : idResolver.apply(document);
                if (StringUtils.hasText(id)) {
                    indexOperation.id(id);
                }
                return indexOperation;
            }));
        }
        return client.bulk(builder.build());
    }
}
