package com.vanta.starter.web.autoconfigure.server;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.util.CollUtils;
import com.vanta.starter.core.util.GeneralPropertySourceFactory;
import io.undertow.Undertow;
import io.undertow.server.handlers.DisallowedMethodsHandler;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Undertow Web 服务器自动配置。
 * <p>
 * 该配置只在 Web 应用、classpath 存在 Undertow 且 {@code server.extension.enabled=true} 时生效。
 * 默认通过 Undertow {@link DisallowedMethodsHandler} 禁止配置中的不安全 HTTP 方法。
 * </p>
 */
@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnClass(Undertow.class)
@EnableConfigurationProperties(ServerExtensionProperties.class)
@PropertySource(value = "classpath:default-server.yml", factory = GeneralPropertySourceFactory.class)
@ConditionalOnProperty(prefix = "server.extension", name = PropertiesConstants.ENABLED, havingValue = "true")
public class UndertowAutoConfiguration {

    /**
     * 当前自动配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(UndertowAutoConfiguration.class);

    /**
     * 创建 Undertow 自定义配置器。
     *
     * @param properties Web 服务器扩展配置属性。
     * @return Undertow Web 服务器工厂定制器。
     */
    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> customize(ServerExtensionProperties properties) {
        return factory -> {
            factory.addDeploymentInfoCustomizers(deploymentInfo -> deploymentInfo
                    .addInitialHandlerChainWrapper(handler -> new DisallowedMethodsHandler(handler, CollUtils
                            .mapToSet(properties.getDisallowedMethods(), HttpString::tryFromString))));
            log.debug("[Vanta Starter] - Disallowed HTTP methods on Server Undertow: {}.", properties
                    .getDisallowedMethods());
            log.debug("[Vanta Starter] - Auto Configuration 'Web-Server Undertow' completed initialization.");
        };
    }
}
