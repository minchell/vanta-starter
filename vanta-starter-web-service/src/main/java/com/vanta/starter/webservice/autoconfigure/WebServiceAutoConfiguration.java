package com.vanta.starter.webservice.autoconfigure;

import com.vanta.starter.apidoc.autoconfigure.SpringDocAutoConfiguration;
import com.vanta.starter.core.autoconfigure.application.ApplicationAutoConfiguration;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.util.GeneralPropertySourceFactory;
import com.vanta.starter.json.jackson.autoconfigure.JacksonAutoConfiguration;
import com.vanta.starter.validation.autoconfigure.ValidationAutoConfiguration;
import com.vanta.starter.web.autoconfigure.cors.CorsAutoConfiguration;
import com.vanta.starter.web.autoconfigure.mvc.WebMvcAutoConfiguration;
import com.vanta.starter.web.autoconfigure.response.GlobalResponseAutoConfiguration;
import com.vanta.starter.web.autoconfigure.server.UndertowAutoConfiguration;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Web 服务基础架构自动配置。
 * <p>
 * 该配置只组合基础 Web 服务能力和默认配置，不引入数据库、缓存、认证、消息等可选能力。
 * </p>
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = PropertiesConstants.WEB_SERVICE, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
@PropertySource(value = "classpath:default-web-service.yml", factory = GeneralPropertySourceFactory.class)
@Import({
        ApplicationAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        ValidationAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        GlobalResponseAutoConfiguration.class,
        CorsAutoConfiguration.class,
        UndertowAutoConfiguration.class,
        SpringDocAutoConfiguration.class
})
public class WebServiceAutoConfiguration {

    /**
     * 当前自动配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(WebServiceAutoConfiguration.class);

    /**
     * 输出 Web 服务基础架构自动配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Web Service' completed initialization.");
    }
}
