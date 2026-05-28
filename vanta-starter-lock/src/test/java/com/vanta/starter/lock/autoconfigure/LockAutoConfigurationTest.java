package com.vanta.starter.lock.autoconfigure;

import com.vanta.starter.lock.core.DistributedLockTemplate;
import com.vanta.starter.lock.core.JdbcLockTemplate;
import com.vanta.starter.lock.core.LocalJvmLockTemplate;
import com.vanta.starter.lock.core.RedisLockTemplate;
import com.vanta.starter.lock.core.ZookeeperLockTemplate;
import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LockAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LockAutoConfiguration.class));

    @Test
    void shouldRegisterLocalTemplateWhenTypeIsLocal() {
        contextRunner
                .withPropertyValues("vanta-starter.lock.enabled=true", "vanta-starter.lock.type=local")
                .run(context -> assertThat(context.getBean(DistributedLockTemplate.class))
                        .isInstanceOf(LocalJvmLockTemplate.class));
    }

    @Test
    void shouldRegisterRedisTemplateWhenTypeIsRedis() {
        contextRunner
                .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
                .withPropertyValues("vanta-starter.lock.enabled=true", "vanta-starter.lock.type=redis")
                .run(context -> assertThat(context.getBean(DistributedLockTemplate.class))
                        .isInstanceOf(RedisLockTemplate.class));
    }

    @Test
    void shouldRegisterZookeeperTemplateWhenTypeIsZookeeper() {
        contextRunner
                .withBean(CuratorFramework.class, () -> mock(CuratorFramework.class))
                .withPropertyValues("vanta-starter.lock.enabled=true", "vanta-starter.lock.type=zookeeper")
                .run(context -> assertThat(context.getBean(DistributedLockTemplate.class))
                        .isInstanceOf(ZookeeperLockTemplate.class));
    }

    @Test
    void shouldRegisterJdbcTemplateWhenTypeIsJdbc() {
        contextRunner
                .withBean(JdbcOperations.class, () -> mock(JdbcOperations.class))
                .withPropertyValues("vanta-starter.lock.enabled=true", "vanta-starter.lock.type=jdbc")
                .run(context -> assertThat(context.getBean(DistributedLockTemplate.class))
                        .isInstanceOf(JdbcLockTemplate.class));
    }
}
