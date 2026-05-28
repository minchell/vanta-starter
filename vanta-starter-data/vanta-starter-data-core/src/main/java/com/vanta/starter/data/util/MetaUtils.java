package com.vanta.starter.data.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.DbUtil;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;
import com.vanta.starter.core.exception.BusinessException;
import com.vanta.starter.data.enums.DatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 数据库元数据工具类。
 * <p>
 * 该工具类基于 JDBC {@link DatabaseMetaData} 和 Hutool 元数据工具读取数据库类型、表信息和列信息。
 * 方法会使用调用方传入的 {@link DataSource} 创建连接，调用方应确保数据源配置可信且连接可用。
 * </p>
 */
public class MetaUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private MetaUtils() {
    }

    /**
     * 获取数据库类型（如果获取不到数据库类型，则返回默认数据库类型）
     *
     * @param dataSource   数据源
     * @param defaultValue 默认数据库类型
     * @return 数据库类型
     */
    public static DatabaseType getDatabaseTypeOrDefault(DataSource dataSource, DatabaseType defaultValue) {
        DatabaseType databaseType = getDatabaseType(dataSource);
        return databaseType == null ? defaultValue : databaseType;
    }

    /**
     * 获取数据库类型
     *
     * @param dataSource 数据源
     * @return 数据库类型
     */
    public static DatabaseType getDatabaseType(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            return DatabaseType.get(databaseProductName);
        } catch (SQLException e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    /**
     * 获取所有表信息
     *
     * @param dataSource 数据源
     * @return 表信息列表
     */
    public static List<Table> getTables(DataSource dataSource) {
        return getTables(dataSource, null);
    }

    /**
     * 获取所有表信息
     *
     * @param dataSource 数据源。
     * @param tableName  表名称；为空时查询全部表。
     * @return 表信息列表。
     */
    public static List<Table> getTables(DataSource dataSource, String tableName) {
        List<Table> tables = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            String catalog = MetaUtil.getCatalog(conn);
            String schema = MetaUtil.getSchema(conn);
            final DatabaseMetaData metaData = conn.getMetaData();
            try (final ResultSet rs = metaData.getTables(catalog, schema, tableName, Convert
                    .toStrArray(TableType.TABLE))) {
                if (rs != null) {
                    String name;
                    while (rs.next()) {
                        name = rs.getString("TABLE_NAME");
                        if (CharSequenceUtil.isNotBlank(name)) {
                            final Table table = Table.create(name);
                            table.setCatalog(catalog);
                            table.setSchema(schema);
                            table.setComment(MetaUtil.getRemarks(metaData, catalog, schema, name));
                            tables.add(table);
                        }
                    }
                }
            }
            return tables;
        } catch (Exception e) {
            throw new DbRuntimeException("Get tables error!", e);
        } finally {
            DbUtil.close(conn);
        }
    }

    /**
     * 获取所有列信息
     *
     * @param dataSource 数据源
     * @param tableName  表名称
     * @return 列信息列表
     */
    public static Collection<Column> getColumns(DataSource dataSource, String tableName) {
        Table table = MetaUtil.getTableMeta(dataSource, tableName);
        return table.getColumns();
    }
}
