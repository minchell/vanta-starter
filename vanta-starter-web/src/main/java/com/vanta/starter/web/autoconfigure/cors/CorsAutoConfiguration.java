package com.vanta.starter.web.autoconfigure.cors;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.constant.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域自动配置。
 * <p>
 * 该配置在 Web 应用且 {@code vanta-starter.web.cors.enabled=true} 或未显式关闭时生效，
 * 根据 {@link CorsProperties} 注册标准 {@link CorsFilter}。
 * </p>
 */
@Lazy
@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = PropertiesConstants.WEB_CORS, name = PropertiesConstants.ENABLED, havingValue = "true")
@EnableConfigurationProperties(CorsProperties.class)
public class CorsAutoConfiguration {

    /**
     * 当前自动配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(CorsAutoConfiguration.class);

    /**
     * 创建跨域过滤器。
     *
     * @param properties CORS 配置属性。
     * @return Spring Web CORS 过滤器。
     */
    @Bean
    @ConditionalOnMissingBean
    public CorsFilter corsFilter(CorsProperties properties) {
        CorsConfiguration config = new CorsConfiguration();
        // 设置跨域允许时间
        config.setMaxAge(1800L);
        // 配置允许跨域的域名
        if (properties.getAllowedOrigins().contains(StringConstants.ASTERISK)) {
            config.addAllowedOriginPattern(StringConstants.ASTERISK);
        } else {
            // 配置为 true 后则必须配置允许跨域的域名，且不允许配置为 *
            config.setAllowCredentials(true);
            properties.getAllowedOrigins().forEach(config::addAllowedOrigin);
        }
        // 配置允许跨域的请求方式
        properties.getAllowedMethods().forEach(config::addAllowedMethod);
        // 配置允许跨域的请求头
        properties.getAllowedHeaders().forEach(config::addAllowedHeader);
        // 配置允许跨域的响应头
        properties.getExposedHeaders().forEach(config::addExposedHeader);
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(StringConstants.PATH_PATTERN, config);
        CorsFilter corsFilter = new CorsFilter(source);
        log.debug("[Vanta Starter] - Auto Configuration 'Web-CorsFilter' completed initialization.");
        return corsFilter;
    }
}
