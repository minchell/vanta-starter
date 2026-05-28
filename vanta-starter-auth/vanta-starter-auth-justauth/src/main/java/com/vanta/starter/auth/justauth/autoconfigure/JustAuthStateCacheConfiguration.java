package com.vanta.starter.auth.justauth.autoconfigure;

import com.vanta.starter.auth.justauth.state.RedisAuthStateCache;
import com.vanta.starter.core.constant.PropertiesConstants;
import me.zhyd.oauth.cache.AuthDefaultStateCache;
import me.zhyd.oauth.cache.AuthStateCache;
import org.redisson.client.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;

/**
 * JustAuth 缓存配置
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 */
abstract class JustAuthStateCacheConfiguration {

    /**
     * log 字段。
     * <p>用于保存 缓存能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(JustAuthStateCacheConfiguration.class);

    /**
     * Redis 缓存
     */
    @ConditionalOnClass(RedisClient.class)
    @ConditionalOnMissingBean(AuthStateCache.class)
    @ConditionalOnProperty(prefix = PropertiesConstants.AUTH_JUSTAUTH, name = "cache.type", havingValue = "redis")
    static class Redis {
        static {
            log.debug("[Vanta Starter] - Auto Configuration 'JustAuth-AuthStateCache-Redis' completed initialization.");
        }

        /**
         * 注册 AuthStateCache 默认 Bean。
         * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
         *
         * @param properties properties 参数，调用方应传入与 缓存能力 场景匹配的有效值
         * @return 方法执行后的结果对象、配置值或运行时依赖
         */
        @Bean
        public AuthStateCache authStateCache(JustAuthProperties properties) {
            return new RedisAuthStateCache(properties.getCache());
        }
    }

    /**
     * 默认缓存
     */
    @ConditionalOnMissingBean(AuthStateCache.class)
    @ConditionalOnProperty(prefix = PropertiesConstants.AUTH_JUSTAUTH, name = "cache.type", havingValue = "default", matchIfMissing = true)
    static class Default {
        static {
            log.debug("[Vanta Starter] - Auto Configuration 'JustAuth-AuthStateCache-Default' completed initialization.");
        }

        /**
         * 注册 AuthStateCache 默认 Bean。
         * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
         *
         * @return 方法执行后的结果对象、配置值或运行时依赖
         */
        @Bean
        public AuthStateCache authStateCache() {
            return AuthDefaultStateCache.INSTANCE;
        }
    }

    /**
     * 自定义缓存
     */
    @ConditionalOnProperty(prefix = PropertiesConstants.AUTH_JUSTAUTH, name = "cache.type", havingValue = "custom")
    static class Custom {
        static {
            log.debug("[Vanta Starter] - Auto Configuration 'JustAuth-AuthStateCache-Custom' completed initialization.");
        }

        /**
         * 注册 AuthStateCache 默认 Bean。
         * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
         *
         * @return 方法执行后的结果对象、配置值或运行时依赖
         */
        @Bean
        @ConditionalOnMissingBean(AuthStateCache.class)
        public AuthStateCache authStateCache() {
            if (log.isErrorEnabled()) {
                log.error("Consider defining a bean of type '{}' in your configuration.", ResolvableType.forClass(AuthStateCache.class));
            }
            throw new NoSuchBeanDefinitionException(AuthStateCache.class);
        }
    }
}
