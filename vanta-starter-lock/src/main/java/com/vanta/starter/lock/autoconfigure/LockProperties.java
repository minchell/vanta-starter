package com.vanta.starter.lock.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Vanta 分布式锁 starter 的统一配置。
 *
 * <p>该配置类只描述锁能力如何启用和选择，不会主动创建远程连接。
 * 远程锁实现依赖业务项目已经提供好的 RedissonClient、CuratorFramework 或 JdbcOperations Bean，
 * 因此 starter 默认不会产生 Redis、Zookeeper、数据库等远程副作用。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.LOCK)
public class LockProperties {

    /**
     * 是否启用锁 starter。
     *
     * <p>true 表示根据 type 注册锁模板；false 表示完全不注册锁模板，避免未接入方被动获得锁能力。</p>
     */
    private boolean enabled = false;

    /**
     * 锁实现类型。
     *
     * <p>支持 local、redis、zookeeper、jdbc。默认 local 只在当前 JVM 内生效，适合开发、测试和单进程保护；
     * 集群部署需要显式切换到远程锁类型。</p>
     */
    private String type = "local";

    /**
     * 锁自动续约或租约时长。
     *
     * <p>Redis 使用该值作为 Redisson tryLock 的 leaseTime；JDBC 使用该值计算锁记录过期时间；
     * Zookeeper 的 InterProcessMutex 由会话生命周期保护，因此该值不参与 Zookeeper 释放逻辑。</p>
     */
    private Duration leaseTime = Duration.ofSeconds(30);

    /**
     * Redis 锁 key 前缀。
     *
     * <p>最终 Redis key 为 redisKeyPrefix + lockKey。前缀用于隔离不同系统、环境和业务域。</p>
     */
    private String redisKeyPrefix = "vanta:lock:";

    /**
     * Zookeeper 锁路径前缀。
     *
     * <p>最终路径为 zookeeperPathPrefix + "/" + 归一化后的 lockKey。前缀必须是绝对路径。</p>
     */
    private String zookeeperPathPrefix = "/vanta/lock";

    /**
     * JDBC 锁表名称。
     *
     * <p>表名会在 JdbcLockTemplate 中做白名单校验，只允许字母、数字和下划线，避免配置被拼接成危险 SQL。</p>
     */
    private String jdbcTableName = "vanta_lock";

    /**
     * JDBC 锁竞争失败后的轮询间隔。
     *
     * <p>waitTime 大于 0 时，JDBC 实现会按该间隔重试抢锁；waitTime 为 0 时只尝试一次。</p>
     */
    private Duration jdbcRetryInterval = Duration.ofMillis(100);

    /**
     * 获取是否启用锁 starter。
     *
     * @return 是否启用锁 starter
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用锁 starter。
     *
     * @param enabled 是否启用锁 starter
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取锁实现类型。
     *
     * @return 锁实现类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置锁实现类型。
     *
     * @param type 锁实现类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取锁自动续约或租约时长。
     *
     * @return 锁自动续约或租约时长
     */
    public Duration getLeaseTime() {
        return leaseTime;
    }

    /**
     * 设置锁自动续约或租约时长。
     *
     * @param leaseTime 锁自动续约或租约时长
     */
    public void setLeaseTime(Duration leaseTime) {
        this.leaseTime = leaseTime;
    }

    /**
     * 获取 Redis 锁 key 前缀。
     *
     * @return Redis 锁 key 前缀
     */
    public String getRedisKeyPrefix() {
        return redisKeyPrefix;
    }

    /**
     * 设置 Redis 锁 key 前缀。
     *
     * @param redisKeyPrefix Redis 锁 key 前缀
     */
    public void setRedisKeyPrefix(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix;
    }

    /**
     * 获取 Zookeeper 锁路径前缀。
     *
     * @return Zookeeper 锁路径前缀
     */
    public String getZookeeperPathPrefix() {
        return zookeeperPathPrefix;
    }

    /**
     * 设置 Zookeeper 锁路径前缀。
     *
     * @param zookeeperPathPrefix Zookeeper 锁路径前缀
     */
    public void setZookeeperPathPrefix(String zookeeperPathPrefix) {
        this.zookeeperPathPrefix = zookeeperPathPrefix;
    }

    /**
     * 获取 JDBC 锁表名称。
     *
     * @return JDBC 锁表名称
     */
    public String getJdbcTableName() {
        return jdbcTableName;
    }

    /**
     * 设置 JDBC 锁表名称。
     *
     * @param jdbcTableName JDBC 锁表名称
     */
    public void setJdbcTableName(String jdbcTableName) {
        this.jdbcTableName = jdbcTableName;
    }

    /**
     * 获取 JDBC 锁竞争失败后的轮询间隔。
     *
     * @return JDBC 锁竞争失败后的轮询间隔
     */
    public Duration getJdbcRetryInterval() {
        return jdbcRetryInterval;
    }

    /**
     * 设置 JDBC 锁竞争失败后的轮询间隔。
     *
     * @param jdbcRetryInterval JDBC 锁竞争失败后的轮询间隔
     */
    public void setJdbcRetryInterval(Duration jdbcRetryInterval) {
        this.jdbcRetryInterval = jdbcRetryInterval;
    }
}
