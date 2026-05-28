package com.vanta.starter.elasticsearch.autoconfigure;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.elasticsearch.core.BulkWriter;
import com.vanta.starter.elasticsearch.core.IndexNameStrategy;
import com.vanta.starter.elasticsearch.core.VantaElasticsearchTemplate;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Elasticsearch 自动配置入口。
 *
 * <p>使用 Elastic Java API Client，不使用已过时的 High Level REST Client。
 * 默认不创建索引、不更新 mapping，只提供安全的客户端和模板封装。</p>
 */
@AutoConfiguration
@ConditionalOnClass(ElasticsearchClient.class)
@EnableConfigurationProperties(ElasticsearchProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.ELASTICSEARCH, name = PropertiesConstants.ENABLED, havingValue = "true")
public class ElasticsearchAutoConfiguration {

    /**
     * 注册 Elasticsearch 低层 RestClient。
     * <p>
     * 仅在显式启用 starter 且业务方未提供 RestClient 时创建；认证头支持 API Key 和 Basic Auth。
     * </p>
     *
     * @param properties Elasticsearch starter 配置
     * @return Elasticsearch 低层 RestClient
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RestClient restClient(ElasticsearchProperties properties) {
        HttpHost[] hosts = properties.getUris().stream().map(HttpHost::create).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(hosts);
        Header authorizationHeader = authorizationHeader(properties);
        if (authorizationHeader != null) {
            builder.setDefaultHeaders(new Header[]{authorizationHeader});
        }
        return builder.build();
    }

    /**
     * 注册 Elastic Java API Client 传输层。
     *
     * @param restClient Elasticsearch 低层 RestClient
     * @return ElasticsearchTransport
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    /**
     * 注册 Elastic Java API Client。
     *
     * @param transport Elasticsearch 传输层
     * @return ElasticsearchClient
     */
    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    /**
     * 注册默认索引名策略。
     *
     * @param properties Elasticsearch starter 配置
     * @return 默认追加配置前缀的索引名策略
     */
    @Bean
    @ConditionalOnMissingBean
    public IndexNameStrategy indexNameStrategy(ElasticsearchProperties properties) {
        return rawIndexName -> properties.getIndexPrefix() + rawIndexName;
    }

    /**
     * 注册 Vanta Elasticsearch 操作模板。
     *
     * @param client            ElasticsearchClient
     * @param indexNameStrategy 索引名策略
     * @return Vanta Elasticsearch 操作模板
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaElasticsearchTemplate vantaElasticsearchTemplate(ElasticsearchClient client, IndexNameStrategy indexNameStrategy) {
        return new VantaElasticsearchTemplate(client, indexNameStrategy);
    }

    /**
     * 注册 Elasticsearch 批量写入工具。
     *
     * @param client            ElasticsearchClient
     * @param indexNameStrategy 索引名策略
     * @return Elasticsearch 批量写入工具
     */
    @Bean
    @ConditionalOnMissingBean
    public BulkWriter bulkWriter(ElasticsearchClient client, IndexNameStrategy indexNameStrategy) {
        return new BulkWriter(client, indexNameStrategy);
    }

    /**
     * 根据配置生成 Elasticsearch 认证请求头。
     *
     * @param properties Elasticsearch starter 配置
     * @return API Key 或 Basic Auth 请求头，未配置认证时返回 {@code null}
     */
    private Header authorizationHeader(ElasticsearchProperties properties) {
        if (StringUtils.hasText(properties.getApiKey())) {
            return new BasicHeader("Authorization", "ApiKey " + properties.getApiKey());
        }
        if (StringUtils.hasText(properties.getUsername()) && StringUtils.hasText(properties.getPassword())) {
            String raw = properties.getUsername() + ":" + properties.getPassword();
            String token = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
            return new BasicHeader("Authorization", "Basic " + token);
        }
        return null;
    }
}
