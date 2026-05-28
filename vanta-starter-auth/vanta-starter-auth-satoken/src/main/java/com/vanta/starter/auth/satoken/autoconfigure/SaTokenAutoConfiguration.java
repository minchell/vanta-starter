package com.vanta.starter.auth.satoken.autoconfigure;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.vanta.starter.auth.satoken.autoconfigure.dao.SaTokenDaoConfiguration;
import com.vanta.starter.core.constant.OrderedConstants;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.core.util.GeneralPropertySourceFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(SaTokenExtensionProperties.class)
@ConditionalOnProperty(prefix = "sa-token.extension", name = PropertiesConstants.ENABLED, havingValue = "true")
@PropertySource(value = "classpath:default-auth-satoken.yml", factory = GeneralPropertySourceFactory.class)
public class SaTokenAutoConfiguration implements WebMvcConfigurer {

    /**
     * log 字段。
     * <p>用于保存 认证授权能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(SaTokenAutoConfiguration.class);
    /**
     * properties 字段。
     * <p>用于保存 认证授权能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final SaTokenExtensionProperties properties;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param properties properties 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     */
    public SaTokenAutoConfiguration(SaTokenExtensionProperties properties) {
        this.properties = properties;
    }

    /**
     * 执行 addInterceptors 逻辑。
     * 该方法属于 认证授权能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param registry registry 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(SpringUtil.getBean(SaInterceptor.class))
                .addPathPatterns(StringConstants.PATH_PATTERN)
                .order(OrderedConstants.Interceptor.AUTH_INTERCEPTOR);
    }

    /**
     * SaToken 拦截器配置
     */
    @Bean
    @ConditionalOnMissingBean
    public SaInterceptor saInterceptor() {
        return new SaInterceptor(handle -> SaRouter.match(StringConstants.PATH_PATTERN)
                .notMatch(properties.getSecurity().getExcludes())
                .check(r -> StpUtil.checkLogin()));
    }

    /**
     * 整合 JWT（简单模式）
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sa-token.extension", name = "enableJwt", havingValue = "true")
    public StpLogic stpLogic() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 注册 void 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'SaToken' completed initialization.");
    }

    /**
     * 持久层配置
     */
    @Configuration
    @Import({SaTokenDaoConfiguration.Default.class, SaTokenDaoConfiguration.Redis.class, SaTokenDaoConfiguration.Custom.class})
    protected static class SaTokenDaoAutoConfiguration {
    }
}
