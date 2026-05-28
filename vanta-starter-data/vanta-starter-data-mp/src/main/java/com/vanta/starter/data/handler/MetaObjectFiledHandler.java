package com.vanta.starter.data.handler;

import com.vanta.starter.core.util.Tuple3;

/**
 * MyBatis-Plus 自动填充字段扩展处理器。
 * <p>
 * 业务方可以继承该类并注册为 Spring Bean，用于向 {@link InjMetaObjectHandler} 提供新增和更新时需要额外填充的字段。
 * 三元组含义依次为：字段名、填充值、已有值非空时是否覆盖。
 * </p>
 */
public abstract class MetaObjectFiledHandler {

    /**
     * 获取新增时需要填充的字段配置。
     *
     * @return 新增填充字段三元组数组。
     */
    public abstract Tuple3<String, Object, Boolean>[] insertFillFieldMap();

    /**
     * 获取更新时需要填充的字段配置。
     *
     * @return 更新填充字段三元组数组。
     */
    public abstract Tuple3<String, Object, Boolean>[] updateFillFieldMap();
}
