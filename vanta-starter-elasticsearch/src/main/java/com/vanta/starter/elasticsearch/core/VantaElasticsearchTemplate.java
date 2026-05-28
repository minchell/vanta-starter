package com.vanta.starter.elasticsearch.core;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Vanta Elasticsearch 操作模板。
 *
 * <p>模板只封装常用索引、文档和查询操作。复杂查询仍建议直接使用
 * Elastic Java API Client，避免 starter 设计成难以理解的万能查询 DSL。</p>
 */
public class VantaElasticsearchTemplate {

    /**
     * Elastic Java API Client。
     * <p>所有索引、文档和搜索操作都通过该客户端执行，业务方可以通过替换 Bean 接管客户端配置。</p>
     */
    private final ElasticsearchClient client;
    /**
     * 索引名策略。
     * <p>用于把业务索引名转换为真实索引名，支持环境前缀、租户前缀或其他隔离规则。</p>
     */
    private final IndexNameStrategy indexNameStrategy;

    /**
     * 创建 Vanta Elasticsearch 操作模板。
     *
     * @param client            Elastic Java API Client
     * @param indexNameStrategy 索引名策略
     */
    public VantaElasticsearchTemplate(ElasticsearchClient client, IndexNameStrategy indexNameStrategy) {
        this.client = client;
        this.indexNameStrategy = indexNameStrategy;
    }

    /**
     * 判断索引是否存在。
     *
     * @param index 业务索引名
     * @return true 表示索引存在
     */
    public boolean indexExists(String index) throws IOException {
        return client.indices().exists(request -> request.index(resolve(index))).value();
    }

    /**
     * 创建索引。
     *
     * @param index 业务索引名
     */
    public void createIndex(String index) throws IOException {
        client.indices().create(request -> request.index(resolve(index)));
    }

    /**
     * 删除索引。
     *
     * @param index 业务索引名
     */
    public void deleteIndex(String index) throws IOException {
        client.indices().delete(request -> request.index(resolve(index)));
    }

    /**
     * 写入文档。
     *
     * @param index    业务索引名
     * @param id       文档 ID
     * @param document 文档对象
     * @return Elastic 原生写入响应
     */
    public <T> IndexResponse indexDocument(String index, String id, T document) throws IOException {
        return client.index(request -> request.index(resolve(index)).id(id).document(document));
    }

    /**
     * 查询文档。
     *
     * @param index        业务索引名
     * @param id           文档 ID
     * @param documentType 文档类型
     * @return Elastic 原生查询响应
     */
    public <T> GetResponse<T> getDocument(String index, String id, Class<T> documentType) throws IOException {
        return client.get(request -> request.index(resolve(index)).id(id), documentType);
    }

    /**
     * 删除文档。
     *
     * @param index 业务索引名
     * @param id    文档 ID
     * @return Elastic 原生删除响应
     */
    public DeleteResponse deleteDocument(String index, String id) throws IOException {
        return client.delete(request -> request.index(resolve(index)).id(id));
    }

    /**
     * 执行搜索。
     *
     * @param index        业务索引名
     * @param documentType 文档类型
     * @param customizer   SearchRequest 构造器定制逻辑
     * @return Elastic 原生搜索响应
     */
    public <T> SearchResponse<T> search(String index, Class<T> documentType, Consumer<SearchRequest.Builder> customizer) throws IOException {
        SearchRequest.Builder builder = new SearchRequest.Builder().index(resolve(index));
        customizer.accept(builder);
        return client.search(builder.build(), documentType);
    }

    /**
     * 解析真实索引名。
     *
     * @param index 业务索引名
     * @return 实际访问 Elasticsearch 的索引名
     */
    private String resolve(String index) {
        return indexNameStrategy.resolve(index);
    }

    /**
     * 获取 Elastic Java API Client。
     *
     * @return Elastic Java API Client
     */
    public ElasticsearchClient getClient() {
        return client;
    }
}
