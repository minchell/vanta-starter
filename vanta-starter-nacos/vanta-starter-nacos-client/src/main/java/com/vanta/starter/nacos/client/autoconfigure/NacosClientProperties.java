package com.vanta.starter.nacos.client.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Vanta Nacos 原生客户端配置。
 *
 * <p>该配置用于创建 Nacos ConfigService 和 NamingService。
 * Spring Cloud Nacos Config/Discovery 仍使用 {@code spring.cloud.nacos.*} 官方配置。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.NACOS_CLIENT)
public class NacosClientProperties {

    /**
     * 是否启用 Vanta Nacos 原生客户端模板。
     */
    private boolean enabled = false;

    /**
     * Nacos 服务端地址，例如 127.0.0.1:8848。
     */
    private String serverAddr = "127.0.0.1:8848";

    /**
     * Nacos namespace。为空时使用 public namespace。
     */
    private String namespace;

    /**
     * Nacos 用户名。建议通过环境变量注入。
     */
    private String username;

    /**
     * Nacos 密码。建议通过环境变量注入。
     */
    private String password;

    /**
     * 读取 Enabled 配置或状态。
     * 该 getter 仅返回 Spring Boot 绑定后的配置值，不触发 Nacos 连接或远程调用。
     *
     * @return 当前配置值
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置 Enabled 配置值。
     * 该 setter 仅用于 Spring Boot 配置绑定和测试装配，不做远程连接或副作用操作。
     *
     * @param enabled 需要绑定的配置值
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 读取 Server Addr 配置或状态。
     * 该 getter 仅返回 Spring Boot 绑定后的配置值，不触发 Nacos 连接或远程调用。
     *
     * @return 当前配置值
     */
    public String getServerAddr() {
        return serverAddr;
    }

    /**
     * 设置 Server Addr 配置值。
     * 该 setter 仅用于 Spring Boot 配置绑定和测试装配，不做远程连接或副作用操作。
     *
     * @param serverAddr 需要绑定的配置值
     */
    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    /**
     * 读取 Namespace 配置或状态。
     * 该 getter 仅返回 Spring Boot 绑定后的配置值，不触发 Nacos 连接或远程调用。
     *
     * @return 当前配置值
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * 设置 Namespace 配置值。
     * 该 setter 仅用于 Spring Boot 配置绑定和测试装配，不做远程连接或副作用操作。
     *
     * @param namespace 需要绑定的配置值
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 读取 Username 配置或状态。
     * 该 getter 仅返回 Spring Boot 绑定后的配置值，不触发 Nacos 连接或远程调用。
     *
     * @return 当前配置值
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置 Username 配置值。
     * 该 setter 仅用于 Spring Boot 配置绑定和测试装配，不做远程连接或副作用操作。
     *
     * @param username 需要绑定的配置值
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 读取 Password 配置或状态。
     * 该 getter 仅返回 Spring Boot 绑定后的配置值，不触发 Nacos 连接或远程调用。
     *
     * @return 当前配置值
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置 Password 配置值。
     * 该 setter 仅用于 Spring Boot 配置绑定和测试装配，不做远程连接或副作用操作。
     *
     * @param password 需要绑定的配置值
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
