package com.vanta.starter.encrypt.api.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * API 加密配置属性
 */
@ConfigurationProperties(PropertiesConstants.ENCRYPT_API)
public class ApiEncryptProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 请求头中 AES 密钥 键名
     */
    private String secretKeyHeader = "X-Api-Encrypt";

    /**
     * 响应加密公钥
     */
    private String publicKey;

    /**
     * 请求解密私钥
     */
    private String privateKey;

    /**
     * 获取是否启用。
     *
     * @return 是否启用
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用。
     *
     * @param enabled 是否启用
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取请求头中 AES 密钥 键名。
     *
     * @return 请求头中 AES 密钥 键名
     */
    public String getSecretKeyHeader() {
        return secretKeyHeader;
    }

    /**
     * 设置请求头中 AES 密钥 键名。
     *
     * @param secretKeyHeader 请求头中 AES 密钥 键名
     */
    public void setSecretKeyHeader(String secretKeyHeader) {
        this.secretKeyHeader = secretKeyHeader;
    }

    /**
     * 获取响应加密公钥。
     *
     * @return 响应加密公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置响应加密公钥。
     *
     * @param publicKey 响应加密公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * 获取请求解密私钥。
     *
     * @return 请求解密私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置请求解密私钥。
     *
     * @param privateKey 请求解密私钥
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
