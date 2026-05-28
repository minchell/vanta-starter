package com.vanta.starter.data.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.vanta.starter.data.autoconfigure.idgenerator.MyBatisPlusIdGeneratorProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * MyBatis-Plus 扩展配置属性。
 * <p>
 * 该类绑定 {@code mybatis-plus.extension} 前缀，用于控制 Mapper 扫描、ID 生成器、分页插件、乐观锁和防全表更新删除插件。
 * 默认 {@link #enabled} 为 {@code false}，避免只引入依赖就改变业务项目的数据访问行为。
 * </p>
 */
@ConfigurationProperties("mybatis-plus.extension")
public class MyBatisPlusExtensionProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * Mapper 接口扫描包（配置时必须使用：mapper-package 键名）
     * <p>
     * e.g. com.example.**.mapper
     * </p>
     */
    private String mapperPackage;

    /**
     * ID 生成器
     */
    @NestedConfigurationProperty
    private MyBatisPlusIdGeneratorProperties idGenerator;

    /**
     * 分页插件配置
     */
    private PaginationProperties pagination;

    /**
     * 启用乐观锁插件
     */
    private boolean optimisticLockerEnabled = false;

    /**
     * 启用防全表更新与删除插件
     */
    private boolean blockAttackPluginEnabled = true;

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
     * 获取 Mapper 接口扫描包（配置时必须使用：mapper-package 键名）。
     *
     * @return Mapper 接口扫描包（配置时必须使用：mapper-package 键名）
     */
    public String getMapperPackage() {
        return mapperPackage;
    }

    /**
     * 设置 Mapper 接口扫描包（配置时必须使用：mapper-package 键名）。
     *
     * @param mapperPackage Mapper 接口扫描包（配置时必须使用：mapper-package 键名）
     */
    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    /**
     * 获取 ID 生成器。
     *
     * @return ID 生成器
     */
    public MyBatisPlusIdGeneratorProperties getIdGenerator() {
        return idGenerator;
    }

    /**
     * 设置 ID 生成器。
     *
     * @param idGenerator ID 生成器
     */
    public void setIdGenerator(MyBatisPlusIdGeneratorProperties idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 获取分页插件配置。
     *
     * @return 分页插件配置
     */
    public PaginationProperties getPagination() {
        return pagination;
    }

    /**
     * 设置分页插件配置。
     *
     * @param pagination 分页插件配置
     */
    public void setPagination(PaginationProperties pagination) {
        this.pagination = pagination;
    }

    /**
     * 获取启用乐观锁插件。
     *
     * @return 启用乐观锁插件
     */
    public boolean isOptimisticLockerEnabled() {
        return optimisticLockerEnabled;
    }

    /**
     * 设置启用乐观锁插件。
     *
     * @param optimisticLockerEnabled 启用乐观锁插件
     */
    public void setOptimisticLockerEnabled(boolean optimisticLockerEnabled) {
        this.optimisticLockerEnabled = optimisticLockerEnabled;
    }

    /**
     * 获取启用防全表更新与删除插件。
     *
     * @return 启用防全表更新与删除插件
     */
    public boolean isBlockAttackPluginEnabled() {
        return blockAttackPluginEnabled;
    }

    /**
     * 设置启用防全表更新与删除插件。
     *
     * @param blockAttackPluginEnabled 启用防全表更新与删除插件
     */
    public void setBlockAttackPluginEnabled(boolean blockAttackPluginEnabled) {
        this.blockAttackPluginEnabled = blockAttackPluginEnabled;
    }

    /**
     * 分页插件配置属性
     */
    public static class PaginationProperties {

        /**
         * 是否启用
         */
        private boolean enabled = true;

        /**
         * 数据库类型
         */
        private DbType dbType;

        /**
         * 是否溢出处理
         */
        private boolean overflow = false;

        /**
         * 单页分页条数限制（默认：-1 表示无限制）
         */
        private Long maxLimit = -1L;

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
         * 获取数据库类型。
         *
         * @return 数据库类型
         */
        public DbType getDbType() {
            return dbType;
        }

        /**
         * 设置数据库类型。
         *
         * @param dbType 数据库类型
         */
        public void setDbType(DbType dbType) {
            this.dbType = dbType;
        }

        /**
         * 获取是否溢出处理。
         *
         * @return 是否溢出处理
         */
        public boolean isOverflow() {
            return overflow;
        }

        /**
         * 设置是否溢出处理。
         *
         * @param overflow 是否溢出处理
         */
        public void setOverflow(boolean overflow) {
            this.overflow = overflow;
        }

        /**
         * 获取单页分页条数限制（默认：-1 表示无限制）。
         *
         * @return 单页分页条数限制（默认：-1 表示无限制）
         */
        public Long getMaxLimit() {
            return maxLimit;
        }

        /**
         * 设置单页分页条数限制（默认：-1 表示无限制）。
         *
         * @param maxLimit 单页分页条数限制（默认：-1 表示无限制）
         */
        public void setMaxLimit(Long maxLimit) {
            this.maxLimit = maxLimit;
        }
    }
}
