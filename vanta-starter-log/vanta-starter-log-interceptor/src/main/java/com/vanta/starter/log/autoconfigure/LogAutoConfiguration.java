package com.vanta.starter.log.autoconfigure;

import com.vanta.starter.core.constant.OrderedConstants;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.log.annotation.ConditionalOnEnabledLog;
import com.vanta.starter.log.dao.LogDao;
import com.vanta.starter.log.dao.impl.DefaultLogDaoImpl;
import com.vanta.starter.log.filter.LogFilter;
import com.vanta.starter.log.handler.InterceptorLogHandler;
import com.vanta.starter.log.handler.LogHandler;
import com.vanta.starter.log.interceptor.LogInterceptor;
import com.vanta.starter.log.model.LogProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Spring MVC 拦截器模式日志自动配置。
 *
 * <p>该模式通过 Servlet Filter 缓存请求/响应，再注册 {@link LogInterceptor} 在 Spring MVC 链路中完成日志记录。
 * 它适合不希望使用 AOP 切点、但仍需要统一记录 Controller 请求日志的项目。</p>
 */
@AutoConfiguration
@ConditionalOnEnabledLog
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class LogAutoConfiguration implements WebMvcConfigurer {

    /**
     * 拦截器日志自动配置日志。
     */
    private static final Logger log = LoggerFactory.getLogger(LogAutoConfiguration.class);

    /**
     * 日志采集配置。
     */
    private final LogProperties logProperties;

    /**
     * 创建拦截器日志自动配置。
     *
     * @param logProperties 日志采集配置
     */
    public LogAutoConfiguration(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    /**
     * 注册日志拦截器到 Spring MVC 链路。
     *
     * @param registry Spring MVC 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor(logProperties, logHandler(), logDao()))
                .addPathPatterns(StringConstants.PATH_PATTERN)
                .excludePathPatterns(logProperties.getExcludePatterns())
                .order(OrderedConstants.Interceptor.LOG_INTERCEPTOR);
    }

    /**
     * 注册日志过滤器。
     *
     * <p>过滤器负责包装请求和响应，保证拦截器可以安全读取请求体、响应体和参数。</p>
     *
     * @return 日志过滤器注册对象
     */
    @Bean
    public FilterRegistrationBean<LogFilter> logFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter(logProperties));
        registrationBean.setOrder(OrderedConstants.Filter.LOG_FILTER);
        registrationBean.addUrlPatterns(StringConstants.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    /**
     * 注册默认拦截器日志处理器。
     *
     * @return 默认拦截器日志处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public LogHandler logHandler() {
        return new InterceptorLogHandler();
    }

    /**
     * 注册默认日志持久层。
     *
     * <p>默认实现通常只提供基础行为，业务项目应通过自定义 {@link LogDao} 对接数据库、消息队列或审计系统。</p>
     *
     * @return 默认日志持久层
     */
    @Bean
    @ConditionalOnMissingBean
    public LogDao logDao() {
        return new DefaultLogDaoImpl();
    }

    /**
     * 输出自动配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Log-Interceptor' completed initialization.");
    }
}
