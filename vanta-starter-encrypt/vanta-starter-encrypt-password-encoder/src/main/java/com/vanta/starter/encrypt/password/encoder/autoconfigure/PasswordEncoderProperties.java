package com.vanta.starter.encrypt.password.encoder.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.encrypt.password.encoder.enums.PasswordEncoderAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 密码编码器配置属性
 */
@ConfigurationProperties(PropertiesConstants.ENCRYPT_PASSWORD_ENCODER)
public class PasswordEncoderProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 默认启用的编码器算法（默认：BCrypt 加密算法）
     */
    private PasswordEncoderAlgorithm algorithm = PasswordEncoderAlgorithm.BCRYPT;

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
     * 获取默认启用的编码器算法（默认：BCrypt 加密算法）。
     *
     * @return 默认启用的编码器算法（默认：BCrypt 加密算法）
     */
    public PasswordEncoderAlgorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * 设置默认启用的编码器算法（默认：BCrypt 加密算法）。
     *
     * @param algorithm 默认启用的编码器算法（默认：BCrypt 加密算法）
     */
    public void setAlgorithm(PasswordEncoderAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
