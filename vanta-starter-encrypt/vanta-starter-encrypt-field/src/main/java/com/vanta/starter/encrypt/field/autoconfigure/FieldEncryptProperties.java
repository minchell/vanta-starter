package com.vanta.starter.encrypt.field.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.encrypt.enums.Algorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 字段加密配置属性
 */
@ConfigurationProperties(PropertiesConstants.ENCRYPT_FIELD)
public class FieldEncryptProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 默认算法
     */
    private Algorithm algorithm = Algorithm.AES;

    /**
     * 对称加密算法密钥
     */
    private String password;

    /**
     * 非对称加密算法公钥
     */
    private String publicKey;

    /**
     * 非对称加密算法私钥
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
     * 获取默认算法。
     *
     * @return 默认算法
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * 设置默认算法。
     *
     * @param algorithm 默认算法
     */
    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 获取对称加密算法密钥。
     *
     * @return 对称加密算法密钥
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置对称加密算法密钥。
     *
     * @param password 对称加密算法密钥
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取非对称加密算法公钥。
     *
     * @return 非对称加密算法公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置非对称加密算法公钥。
     *
     * @param publicKey 非对称加密算法公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * 获取非对称加密算法私钥。
     *
     * @return 非对称加密算法私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置非对称加密算法私钥。
     *
     * @param privateKey 非对称加密算法私钥
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
