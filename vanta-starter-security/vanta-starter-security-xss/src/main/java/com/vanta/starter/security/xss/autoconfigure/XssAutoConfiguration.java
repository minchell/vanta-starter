package com.vanta.starter.security.xss.autoconfigure;

import com.vanta.starter.core.constant.OrderedConstants;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.security.xss.filter.XssFilter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * XSS 过滤自动配置
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(XssProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_XSS, name = PropertiesConstants.ENABLED, havingValue = "true")
public class XssAutoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 安全防护能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(XssAutoConfiguration.class);

    /**
     * XSS 过滤器
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilter(XssProperties xssProperties) {
        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssFilter(xssProperties));
        registrationBean.setOrder(OrderedConstants.Filter.XSS_FILTER);
        registrationBean.addUrlPatterns(StringConstants.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    /**
     * 执行 postConstruct 逻辑。
     * 该方法属于 安全防护能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Security-XSS' completed initialization.");
    }
}
