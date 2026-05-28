package com.vanta.starter.zookeeper.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.zookeeper.core.VantaZookeeperTemplate;
import com.vanta.starter.zookeeper.core.ZookeeperLockTemplate;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * Zookeeper 自动配置入口。
 *
 * <p>自动配置使用 CuratorFramework 作为底层客户端，并提供节点操作模板和分布式锁模板。
 * 业务可以自定义 CuratorFramework Bean 完全接管连接创建。</p>
 */
@AutoConfiguration
@ConditionalOnClass(CuratorFramework.class)
@EnableConfigurationProperties(ZookeeperProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.ZOOKEEPER, name = PropertiesConstants.ENABLED, havingValue = "true")
public class ZookeeperAutoConfiguration {

    /**
     * 注册 CuratorFramework 客户端。
     * <p>
     * 默认只在显式启用 Zookeeper starter 且业务方未提供同类型 Bean 时创建，支持 namespace、重试策略和 ACL 认证。
     * </p>
     *
     * @param properties Zookeeper starter 配置
     * @return CuratorFramework 客户端
     */
    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnMissingBean
    public CuratorFramework curatorFramework(ZookeeperProperties properties) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                (int) properties.getBaseSleepTime().toMillis(),
                properties.getMaxRetries()
        );

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(properties.getConnectString())
                .sessionTimeoutMs((int) properties.getSessionTimeout().toMillis())
                .connectionTimeoutMs((int) properties.getConnectionTimeout().toMillis())
                .retryPolicy(retryPolicy);

        if (StringUtils.hasText(properties.getNamespace())) {
            builder.namespace(properties.getNamespace());
        }
        if (StringUtils.hasText(properties.getAuthScheme()) && StringUtils.hasText(properties.getAuth())) {
            builder.authorization(properties.getAuthScheme(), properties.getAuth().getBytes(StandardCharsets.UTF_8));
        }

        return builder.build();
    }

    /**
     * 注册 Zookeeper 节点操作模板。
     *
     * @param curatorFramework CuratorFramework 客户端
     * @return Zookeeper 节点操作模板
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaZookeeperTemplate vantaZookeeperTemplate(CuratorFramework curatorFramework) {
        return new VantaZookeeperTemplate(curatorFramework);
    }

    /**
     * 注册 Zookeeper 分布式锁模板。
     *
     * @param curatorFramework CuratorFramework 客户端
     * @return Zookeeper 分布式锁模板
     */
    @Bean
    @ConditionalOnMissingBean
    public ZookeeperLockTemplate zookeeperLockTemplate(CuratorFramework curatorFramework) {
        return new ZookeeperLockTemplate(curatorFramework);
    }
}
