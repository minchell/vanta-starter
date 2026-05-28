package com.vanta.starter.auth.satoken.autoconfigure;

import com.vanta.starter.auth.satoken.autoconfigure.dao.SaTokenDaoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * SaToken 扩展配置属性
 */
@ConfigurationProperties("sa-token.extension")
public class SaTokenExtensionProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 启用 JWT
     */
    private boolean enableJwt = false;

    /**
     * 持久层配置
     */
    @NestedConfigurationProperty
    private SaTokenDaoProperties dao;

    /**
     * 安全配置
     */
    @NestedConfigurationProperty
    private SaTokenSecurityProperties security;

    /**
     * 获取是否启用。
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用。
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取启用 JWT。
     *
     * @return 启用 JWT
     */
    public boolean isEnableJwt() {
        return enableJwt;
    }

    /**
     * 设置启用 JWT。
     *
     * @param enableJwt 启用 JWT
     */
    public void setEnableJwt(boolean enableJwt) {
        this.enableJwt = enableJwt;
    }

    /**
     * 获取持久层配置。
     *
     * @return 持久层配置
     */
    public SaTokenDaoProperties getDao() {
        return dao;
    }

    /**
     * 设置持久层配置。
     *
     * @param dao 持久层配置
     */
    public void setDao(SaTokenDaoProperties dao) {
        this.dao = dao;
    }

    /**
     * 获取安全配置。
     *
     * @return 安全配置
     */
    public SaTokenSecurityProperties getSecurity() {
        return security;
    }

    /**
     * 设置安全配置。
     *
     * @param security 安全配置
     */
    public void setSecurity(SaTokenSecurityProperties security) {
        this.security = security;
    }
}
