package com.vanta.starter.web.autoconfigure.mvc;

import com.vanta.starter.web.autoconfigure.filter.RepeatableReadFilter;
import com.vanta.starter.web.autoconfigure.mvc.converter.BaseEnumConverterFactory;
import com.vanta.starter.web.autoconfigure.mvc.converter.time.DateConverter;
import com.vanta.starter.web.autoconfigure.mvc.converter.time.LocalDateConverter;
import com.vanta.starter.web.autoconfigure.mvc.converter.time.LocalDateTimeConverter;
import com.vanta.starter.web.autoconfigure.mvc.converter.time.LocalTimeConverter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Web MVC 自动配置。
 * <p>
 * 该配置统一注册 HTTP 消息转换器、基础类型转换器、业务拦截器扩展点和请求体可重复读取过滤器。
 * 业务方可以通过注册 {@link IRegistration} Bean 增加拦截器，也可以覆盖相关 Bean 调整默认行为。
 * </p>
 */
@AutoConfiguration
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    /**
     * 当前自动配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(WebMvcAutoConfiguration.class);

    /**
     * Jackson HTTP 消息转换器。
     */
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    /**
     * 拦截器注册扩展点列表。
     */
    private final List<IRegistration> registrations;

    /**
     * 创建 Web MVC 自动配置。
     *
     * @param mappingJackson2HttpMessageConverter Jackson HTTP 消息转换器。
     * @param registrations                       拦截器注册扩展点列表。
     */
    public WebMvcAutoConfiguration(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter, List<IRegistration> registrations) {
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
        this.registrations = registrations;
    }

    /**
     * 解决 Jackson2ObjectMapperBuilderCustomizer 配置不生效的问题
     * <p>
     * MappingJackson2HttpMessageConverter 对象在程序启动时创建了多个，移除多余的，保证只有一个
     * </p>
     *
     * @param converters Spring MVC 当前 HTTP 消息转换器列表。
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
        if (Objects.isNull(mappingJackson2HttpMessageConverter)) {
            converters.add(0, new MappingJackson2HttpMessageConverter());
        } else {
            converters.add(0, mappingJackson2HttpMessageConverter);
        }

        // 解决 JSON parse error: Cannot deserialize value of type `java.lang.String` from Object value (token `JsonToken.START_OBJECT`)
        // MappingJackson2HttpMessageConverter 抢先执行，导致 StringHttpMessageConverter 无法正确处理字符串类型的请求体
        converters.removeIf(c -> c instanceof StringHttpMessageConverter);
        converters.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        // 自定义 converters 时，需要手动在最前面添加 ByteArrayHttpMessageConverter
        // 否则 Spring Doc OpenAPI 的 /*/api-docs/**（例如：/v3/api-docs/default）接口响应内容会变为 Base64 编码后的内容，最终导致接口文档解析失败
        // 详情请参阅：https://github.com/springdoc/springdoc-openapi/issues/2143
        converters.add(0, new ByteArrayHttpMessageConverter());
    }

    /**
     * 注册 Web MVC 类型转换器。
     *
     * @param registry Spring 格式化转换器注册表。
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new BaseEnumConverterFactory());
        registry.addConverter(new DateConverter());
        registry.addConverter(new LocalDateTimeConverter());
        registry.addConverter(new LocalDateConverter());
        registry.addConverter(new LocalTimeConverter());
    }

    /**
     * 注册业务方通过 {@link IRegistration} 暴露的拦截器。
     *
     * @param registry Spring MVC 拦截器注册表。
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!CollectionUtils.isEmpty(this.registrations)) {
            this.registrations.forEach(cfg -> {
                HandlerInterceptor handlerInterceptor = cfg.getHandlerInterceptor();

                if (handlerInterceptor == null) {
                    return;
                }

                InterceptorRegistration registration = registry.addInterceptor(handlerInterceptor);

                List<String> includes = cfg.getIncludePatterns();

                if (CollectionUtils.isEmpty(includes)) {
                    registration.addPathPatterns("/**");
                } else {
                    registration.addPathPatterns(includes);
                }

                if (!CollectionUtils.isEmpty(cfg.getExcludePatterns())) {
                    registration.excludePathPatterns(cfg.getExcludePatterns());
                }

                if (cfg.getOrder() != null) {
                    registration.order(cfg.getOrder());
                }

                if (cfg.getPathMatcher() != null) {
                    registration.pathMatcher(cfg.getPathMatcher());
                }

            });
        }
    }

    /**
     * 注册请求体可重复读取过滤器。
     *
     * @return 请求体可重复读取过滤器注册 Bean。
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<RepeatableReadFilter> repeatableBodyFilter() {
        FilterRegistrationBean<RepeatableReadFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RepeatableReadFilter());
        registration.addUrlPatterns("/*");
        registration.setName("repeatableReadFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 输出 Web MVC 自动配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Web MVC' completed initialization.");
    }
}
