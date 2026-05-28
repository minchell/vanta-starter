package com.vanta.starter.auth.satoken.autoconfigure.dao;

import com.vanta.starter.auth.satoken.enums.SaTokenDaoType;

/**
 * SaToken 持久层配置属性
 */
public class SaTokenDaoProperties {

    /**
     * 持久层类型
     */
    private SaTokenDaoType type = SaTokenDaoType.DEFAULT;

    /**
     * 获取持久层类型。
     *
     * @return 持久层类型
     */
    public SaTokenDaoType getType() {
        return type;
    }

    /**
     * 设置持久层类型。
     *
     * @param type 持久层类型
     */
    public void setType(SaTokenDaoType type) {
        this.type = type;
    }
}
