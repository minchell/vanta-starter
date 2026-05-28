package com.vanta.starter.lock.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.lock.core.DistributedLockTemplate;
import com.vanta.starter.lock.core.JdbcLockTemplate;
import com.vanta.starter.lock.core.LocalJvmLockTemplate;
import com.vanta.starter.lock.core.RedisLockTemplate;
import com.vanta.starter.lock.core.ZookeeperLockTemplate;
import org.apache.curator.framework.CuratorFramework;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * Vanta 分布式锁 starter 自动配置。
 *
 * <p>自动配置只负责把统一锁模板接入 Spring 容器。默认 type=local 时不会访问任何远程服务；
 * redis、zookeeper、jdbc 三种类型必须同时满足显式配置 type 和业务方提供底层 Bean，才会创建对应模板。</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(LockProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.LOCK, name = PropertiesConstants.ENABLED, havingValue = "true")
public class LockAutoConfiguration {

    /**
     * 注册默认 JVM 本地锁模板。
     *
     * <p>该 Bean 只在业务方没有自定义 {@link DistributedLockTemplate} 且 type=local 时创建。
     * 它适合本地开发、单元测试和单进程任务保护，不具备跨 JVM 互斥语义。</p>
     *
     * @return JVM 内本地锁模板
     */
    @Bean
    @ConditionalOnMissingBean(DistributedLockTemplate.class)
    @ConditionalOnProperty(prefix = PropertiesConstants.LOCK, name = "type", havingValue = "local", matchIfMissing = true)
    public DistributedLockTemplate localLockTemplate() {
        return new LocalJvmLockTemplate();
    }

    /**
     * 注册 Redis 分布式锁模板。
     *
     * <p>该 Bean 依赖业务方已经配置好的 RedissonClient。starter 不创建 Redis 连接，避免默认产生远程副作用。</p>
     *
     * @param redissonClient Redisson 客户端
     * @param properties     锁配置
     * @return Redis 分布式锁模板
     */
    @Bean
    @ConditionalOnClass(RedissonClient.class)
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean(DistributedLockTemplate.class)
    @ConditionalOnProperty(prefix = PropertiesConstants.LOCK, name = "type", havingValue = "redis")
    public DistributedLockTemplate redisLockTemplate(RedissonClient redissonClient, LockProperties properties) {
        return new RedisLockTemplate(redissonClient, properties);
    }

    /**
     * 注册 Zookeeper 分布式锁模板。
     *
     * <p>该 Bean 依赖业务方已经配置好的 CuratorFramework。starter 不创建 Zookeeper 连接，
     * 只复用现有客户端执行互斥逻辑。</p>
     *
     * @param curatorFramework Curator 客户端
     * @param properties       锁配置
     * @return Zookeeper 分布式锁模板
     */
    @Bean
    @ConditionalOnClass(CuratorFramework.class)
    @ConditionalOnBean(CuratorFramework.class)
    @ConditionalOnMissingBean(DistributedLockTemplate.class)
    @ConditionalOnProperty(prefix = PropertiesConstants.LOCK, name = "type", havingValue = "zookeeper")
    public DistributedLockTemplate zookeeperLockTemplate(CuratorFramework curatorFramework, LockProperties properties) {
        return new ZookeeperLockTemplate(curatorFramework, properties);
    }

    /**
     * 注册 JDBC 分布式锁模板。
     *
     * <p>该 Bean 依赖业务方已经配置好的 JdbcOperations。starter 不创建数据源，也不自动建表；
     * 业务系统需要按 README 中的表结构自行维护 DDL。</p>
     *
     * @param jdbcOperations Spring JDBC 操作入口
     * @param properties     锁配置
     * @return JDBC 分布式锁模板
     */
    @Bean
    @ConditionalOnClass(JdbcOperations.class)
    @ConditionalOnBean(JdbcOperations.class)
    @ConditionalOnMissingBean(DistributedLockTemplate.class)
    @ConditionalOnProperty(prefix = PropertiesConstants.LOCK, name = "type", havingValue = "jdbc")
    public DistributedLockTemplate jdbcLockTemplate(JdbcOperations jdbcOperations, LockProperties properties) {
        return new JdbcLockTemplate(jdbcOperations, properties);
    }
}
