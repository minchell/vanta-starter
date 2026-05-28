package com.vanta.starter.zookeeper.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Vanta Zookeeper 配置。
 *
 * <p>默认不启用远程连接，业务必须显式配置 enabled=true。
 * 该配置只封装 Curator 客户端常用参数，复杂场景可以通过自定义 CuratorFramework Bean 覆盖。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.ZOOKEEPER)
public class ZookeeperProperties {

    /**
     * 是否启用 Zookeeper 自动配置。
     */
    private boolean enabled = false;

    /**
     * Zookeeper 连接串，例如 127.0.0.1:2181。
     */
    private String connectString = "127.0.0.1:2181";

    /**
     * Curator namespace，用于把当前应用的节点隔离到同一个根路径下。
     */
    private String namespace;

    /**
     * session 超时时间。
     */
    private Duration sessionTimeout = Duration.ofSeconds(15);

    /**
     * connection 超时时间。
     */
    private Duration connectionTimeout = Duration.ofSeconds(10);

    /**
     * 指数退避重试的基础等待时间。
     */
    private Duration baseSleepTime = Duration.ofSeconds(1);

    /**
     * 最大重试次数。
     */
    private int maxRetries = 3;

    /**
     * ACL 认证 scheme，例如 digest。
     */
    private String authScheme;

    /**
     * ACL 认证内容，例如 username:password。建议通过环境变量注入。
     */
    private String auth;

    /**
     * 获取是否启用 Zookeeper 自动配置。
     *
     * @return 是否启用 Zookeeper 自动配置
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 Zookeeper 自动配置。
     *
     * @param enabled 是否启用 Zookeeper 自动配置
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取 Zookeeper 连接串，例如 127.0.0.1:2181。
     *
     * @return Zookeeper 连接串，例如 127.0.0.1:2181
     */
    public String getConnectString() {
        return connectString;
    }

    /**
     * 设置 Zookeeper 连接串，例如 127.0.0.1:2181。
     *
     * @param connectString Zookeeper 连接串，例如 127.0.0.1:2181
     */
    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    /**
     * 获取 Curator namespace，用于把当前应用的节点隔离到同一个根路径下。
     *
     * @return Curator namespace，用于把当前应用的节点隔离到同一个根路径下
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * 设置 Curator namespace，用于把当前应用的节点隔离到同一个根路径下。
     *
     * @param namespace Curator namespace，用于把当前应用的节点隔离到同一个根路径下
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 获取 session 超时时间。
     *
     * @return session 超时时间
     */
    public Duration getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * 设置 session 超时时间。
     *
     * @param sessionTimeout session 超时时间
     */
    public void setSessionTimeout(Duration sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    /**
     * 获取 connection 超时时间。
     *
     * @return connection 超时时间
     */
    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置 connection 超时时间。
     *
     * @param connectionTimeout connection 超时时间
     */
    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 获取指数退避重试的基础等待时间。
     *
     * @return 指数退避重试的基础等待时间
     */
    public Duration getBaseSleepTime() {
        return baseSleepTime;
    }

    /**
     * 设置指数退避重试的基础等待时间。
     *
     * @param baseSleepTime 指数退避重试的基础等待时间
     */
    public void setBaseSleepTime(Duration baseSleepTime) {
        this.baseSleepTime = baseSleepTime;
    }

    /**
     * 获取最大重试次数。
     *
     * @return 最大重试次数
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * 设置最大重试次数。
     *
     * @param maxRetries 最大重试次数
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * 获取 ACL 认证 scheme，例如 digest。
     *
     * @return ACL 认证 scheme，例如 digest
     */
    public String getAuthScheme() {
        return authScheme;
    }

    /**
     * 设置 ACL 认证 scheme，例如 digest。
     *
     * @param authScheme ACL 认证 scheme，例如 digest
     */
    public void setAuthScheme(String authScheme) {
        this.authScheme = authScheme;
    }

    /**
     * 获取 ACL 认证内容，例如 username:password。建议通过环境变量注入。
     *
     * @return ACL 认证内容，例如 username:password。建议通过环境变量注入
     */
    public String getAuth() {
        return auth;
    }

    /**
     * 设置 ACL 认证内容，例如 username:password。建议通过环境变量注入。
     *
     * @param auth ACL 认证内容，例如 username:password。建议通过环境变量注入
     */
    public void setAuth(String auth) {
        this.auth = auth;
    }
}
