package com.vanta.starter.auth.justauth.autoconfigure;

import com.vanta.starter.auth.justauth.AuthRequestFactory;
import com.vanta.starter.core.constant.PropertiesConstants;
import jakarta.annotation.PostConstruct;
import me.zhyd.oauth.cache.AuthStateCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * JustAuth 自动配置
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 */
@AutoConfiguration
@EnableConfigurationProperties(JustAuthProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.AUTH_JUSTAUTH, name = PropertiesConstants.ENABLED, havingValue = "true")
public class JustAuthAutoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 认证授权能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(JustAuthAutoConfiguration.class);

    /**
     * AuthRequest 工厂配置
     */
    @Bean
    public AuthRequestFactory authRequestFactory(JustAuthProperties properties, AuthStateCache stateCache) {
        return new AuthRequestFactory(properties, stateCache);
    }

    /**
     * 执行 postConstruct 逻辑。
     * 该方法属于 认证授权能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'JustAuth' completed initialization.");
    }

    /**
     * 缓存自动配置
     */
    @Configuration
    @Import({
            JustAuthStateCacheConfiguration.Default.class,
            JustAuthStateCacheConfiguration.Redis.class,
            JustAuthStateCacheConfiguration.Custom.class
    })
    protected static class AuthStateCacheAutoConfiguration {
    }
}
