package com.vanta.starter.auth.satoken.autoconfigure;

/**
 * SaToken 安全配置属性
 */
public class SaTokenSecurityProperties {

    /**
     * 排除（放行）路径配置
     */
    private String[] excludes = new String[0];

    /**
     * 获取排除（放行）路径配置。
     *
     * @return 排除（放行）路径配置
     */
    public String[] getExcludes() {
        return excludes;
    }

    /**
     * 设置排除（放行）路径配置。
     *
     * @param excludes 排除（放行）路径配置
     */
    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }
}
