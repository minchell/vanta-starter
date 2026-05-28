package com.vanta.starter.elasticsearch.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Vanta Elasticsearch 配置。
 *
 * <p>默认不启用远程连接，也不自动创建索引或更新 mapping。
 * 生产环境必须显式开启并配置访问地址。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.ELASTICSEARCH)
public class ElasticsearchProperties {

    /**
     * 是否启用 Vanta Elasticsearch 自动配置。
     */
    private boolean enabled = false;

    /**
     * Elasticsearch 节点地址，例如 http://127.0.0.1:9200。
     */
    private List<String> uris = new ArrayList<>(List.of("http://127.0.0.1:9200"));

    /**
     * Basic auth 用户名。
     */
    private String username;

    /**
     * Basic auth 密码。
     */
    private String password;

    /**
     * Elastic API Key。配置后优先于 basic auth。
     */
    private String apiKey;

    /**
     * 是否允许 starter 自动创建索引。默认关闭，避免生产误操作。
     */
    private boolean autoCreateIndex = false;

    /**
     * 索引名前缀，适合按环境隔离，例如 dev_、test_。
     */
    private String indexPrefix = "";

    /**
     * 获取是否启用 Vanta Elasticsearch 自动配置。
     *
     * @return 是否启用 Vanta Elasticsearch 自动配置
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 Vanta Elasticsearch 自动配置。
     *
     * @param enabled 是否启用 Vanta Elasticsearch 自动配置
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取 Elasticsearch 节点地址，例如 http://127.0.0.1:9200。
     *
     * @return Elasticsearch 节点地址，例如 http://127.0.0.1:9200
     */
    public List<String> getUris() {
        return uris;
    }

    /**
     * 设置 Elasticsearch 节点地址，例如 http://127.0.0.1:9200。
     *
     * @param uris Elasticsearch 节点地址，例如 http://127.0.0.1:9200
     */
    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    /**
     * 获取 Basic auth 用户名。
     *
     * @return Basic auth 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置 Basic auth 用户名。
     *
     * @param username Basic auth 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取 Basic auth 密码。
     *
     * @return Basic auth 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置 Basic auth 密码。
     *
     * @param password Basic auth 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取 Elastic API Key。配置后优先于 basic auth。
     *
     * @return Elastic API Key。配置后优先于 basic auth
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * 设置 Elastic API Key。配置后优先于 basic auth。
     *
     * @param apiKey Elastic API Key。配置后优先于 basic auth
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 获取是否允许 starter 自动创建索引。默认关闭，避免生产误操作。
     *
     * @return 是否允许 starter 自动创建索引。默认关闭，避免生产误操作
     */
    public boolean isAutoCreateIndex() {
        return autoCreateIndex;
    }

    /**
     * 设置是否允许 starter 自动创建索引。默认关闭，避免生产误操作。
     *
     * @param autoCreateIndex 是否允许 starter 自动创建索引。默认关闭，避免生产误操作
     */
    public void setAutoCreateIndex(boolean autoCreateIndex) {
        this.autoCreateIndex = autoCreateIndex;
    }

    /**
     * 获取索引名前缀，适合按环境隔离，例如 dev_、test_。
     *
     * @return 索引名前缀，适合按环境隔离，例如 dev_、test_
     */
    public String getIndexPrefix() {
        return indexPrefix;
    }

    /**
     * 设置索引名前缀，适合按环境隔离，例如 dev_、test_。
     *
     * @param indexPrefix 索引名前缀，适合按环境隔离，例如 dev_、test_
     */
    public void setIndexPrefix(String indexPrefix) {
        this.indexPrefix = indexPrefix;
    }
}
