package com.vanta.starter.idempotent.autoconfigure;

import com.vanta.starter.cache.redis.autoconfigure.RedissonAutoConfiguration;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.idempotent.aop.IdempotentAspect;
import com.vanta.starter.idempotent.generator.DefaultIdempotentNameGenerator;
import com.vanta.starter.idempotent.generator.IdempotentNameGenerator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * IdempotentAutoConfiguration 类。
 * <p>该类型属于 幂等能力，负责根据 classpath、配置开关和缺省 Bean 条件装配 starter 默认能力。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@AutoConfiguration(after = RedissonAutoConfiguration.class)
@EnableConfigurationProperties(IdempotentProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.IDEMPOTENT, name = PropertiesConstants.ENABLED, havingValue = "true")
public class IdempotentAutoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 幂等能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(IdempotentAutoConfiguration.class);

    /**
     * 幂等切面
     */
    @Bean
    public IdempotentAspect idempotentAspect(IdempotentProperties properties,
                                             IdempotentNameGenerator idempotentNameGenerator) {
        return new IdempotentAspect(properties, idempotentNameGenerator);
    }

    /**
     * 幂等名称生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentNameGenerator idempotentNameGenerator() {
        return new DefaultIdempotentNameGenerator();
    }

    /**
     * 注册 void 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Idempotent' completed initialization.");
    }
}
