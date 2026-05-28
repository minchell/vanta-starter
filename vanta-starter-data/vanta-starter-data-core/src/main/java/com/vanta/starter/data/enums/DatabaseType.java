package com.vanta.starter.data.enums;

import com.vanta.starter.data.function.ISqlFunction;

import java.io.Serializable;

/**
 * 数据库类型枚举。
 * <p>
 * 该枚举抽象不同数据库方言下的 SQL 函数差异，目前主要用于生成“集合字符串包含值”的 SQL 片段。
 * 新增数据库类型时，应同时实现 {@link #findInSet(Serializable, String)}。
 * </p>
 */
public enum DatabaseType implements ISqlFunction {

    /**
     * MySQL
     */
    MYSQL("MySQL") {
        /**
         * 生成 MySQL find_in_set 查询片段。
         *
         * @param value 待查询值。
         * @param set   存储集合字符串的列或表达式。
         * @return MySQL 可执行的 SQL 条件片段。
         */
        @Override
        public String findInSet(Serializable value, String set) {
            return "find_in_set('%s', %s) <> 0".formatted(value, set);
        }
    },

    /**
     * PostgreSQL
     */
    POSTGRESQL("PostgreSQL") {
        /**
         * 生成 PostgreSQL 字符串位置查询片段。
         *
         * @param value 待查询值。
         * @param set   存储集合字符串的列或表达式。
         * @return PostgreSQL 可执行的 SQL 条件片段。
         */
        @Override
        public String findInSet(Serializable value, String set) {
            return "(select position(',%s,' in ','||%s||',')) <> 0".formatted(value, set);
        }
    };

    /**
     * PostgreSQL 兼容别名。
     * <p>
     * 新代码应使用 {@link #POSTGRESQL}，该别名仅用于兼容历史调用方。
     * </p>
     */
    @Deprecated(since = "0.0.1", forRemoval = false)
    public static final DatabaseType POSTGRE_SQL = POSTGRESQL;

    /**
     * 数据库产品名称。
     */
    private final String database;

    /**
     * 创建数据库类型。
     *
     * @param database 数据库产品名称。
     */
    DatabaseType(String database) {
        this.database = database;
    }

    /**
     * 获取数据库类型
     *
     * @param database 数据库产品名称。
     * @return 匹配的数据库类型；无法匹配时返回 {@code null}。
     */
    public static DatabaseType get(String database) {
        for (DatabaseType databaseType : DatabaseType.values()) {
            if (databaseType.database.equalsIgnoreCase(database)) {
                return databaseType;
            }
        }
        return null;
    }

    /**
     * 获取数据库产品名称。
     *
     * @return 数据库产品名称。
     */
    public String getDatabase() {
        return database;
    }
}
