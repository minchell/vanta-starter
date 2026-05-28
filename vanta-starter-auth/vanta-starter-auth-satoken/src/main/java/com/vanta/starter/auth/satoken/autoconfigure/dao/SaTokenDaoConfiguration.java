package com.vanta.starter.auth.satoken.autoconfigure.dao;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import com.vanta.starter.cache.redis.autoconfigure.RedissonAutoConfiguration;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;

/**
 * SaToken 持久层配置
 */
public class SaTokenDaoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 认证授权能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(SaTokenDaoConfiguration.class);

    private SaTokenDaoConfiguration() {
    }

    /**
     * 自定义持久层实现-默认（内存）
     */
    @ConditionalOnMissingBean(SaTokenDao.class)
    @ConditionalOnProperty(name = "sa-token.extension.dao.type", havingValue = "default", matchIfMissing = true)
    public static class Default {
        static {
            log.debug("[Vanta Starter] - Auto Configuration 'SaToken-Dao-Default' completed initialization.");
        }

        /**
         * 注册 SaTokenDao 默认 Bean。
         * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
         *
         * @return 方法执行后的结果对象、配置值或运行时依赖
         */
        @Bean
        public SaTokenDao saTokenDao() {
            return new SaTokenDaoDefaultImpl();
        }
    }

    /**
     * 自定义持久层实现-Redis（默认）
     */
    @ConditionalOnMissingBean(SaTokenDao.class)
    @AutoConfigureAfter(RedissonAutoConfiguration.class)
    @ConditionalOnProperty(name = "sa-token.extension.dao.type", havingValue = "redis")
    public static class Redis {
        static {
            log.debug("[Vanta Starter] - Auto Configuration 'SaToken-Dao-Redis' completed initialization.");
        }

        /**
         * 注册 SaTokenDao 默认 Bean。
         * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
         *
         * @param redissonClient redissonClient 参数，调用方应传入与 认证授权能力 场景匹配的有效值
         * @return 方法执行后的结果对象、配置值或运行时依赖
         */
        @Bean
        public SaTokenDao saTokenDao(RedissonClient redissonClient) {
            return new SaTokenDaoForRedisson(redissonClient);
        }
    }

    /**
     * 自定义持久层实现
     */
    @ConditionalOnProperty(name = "sa-token.extension.dao.type", havingValue = "custom")
    public static class Custom {

        /**
         * 注册 SaTokenDao 默认 Bean。
         * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
         *
         * @return 方法执行后的结果对象、配置值或运行时依赖
         */
        @Bean
        @ConditionalOnMissingBean
        public SaTokenDao saTokenDao() {
            if (log.isErrorEnabled()) {
                log.error("Consider defining a bean of type '{}' in your configuration.", ResolvableType.forClass(SaTokenDao.class));
            }
            throw new NoSuchBeanDefinitionException(SaTokenDao.class);
        }
    }
}
