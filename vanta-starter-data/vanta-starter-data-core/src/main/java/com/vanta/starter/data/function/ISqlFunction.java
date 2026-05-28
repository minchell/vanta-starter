package com.vanta.starter.data.function;

import java.io.Serializable;

/**
 * SQL 方言函数契约。
 * <p>
 * 不同数据库对同一语义函数的 SQL 写法可能不同，该接口用于统一暴露方言相关 SQL 片段生成能力。
 * 调用方必须保证传入值和列名来自可信路径，避免拼接任意用户输入造成 SQL 注入风险。
 * </p>
 */
public interface ISqlFunction {

    /**
     * find_in_set 函数
     *
     * @param value 值
     * @param set   集合
     * @return 函数实现
     */
    String findInSet(Serializable value, String set);
}
