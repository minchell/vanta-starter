package com.vanta.starter.trace.autoconfigure;

import com.vanta.starter.core.constant.OrderedConstants;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.trace.filter.TLogServletFilter;
import com.vanta.starter.trace.handler.TraceIdGenerator;
import com.yomahub.tlog.id.TLogIdGenerator;
import com.yomahub.tlog.id.TLogIdGeneratorLoader;
import com.yomahub.tlog.spring.TLogPropertyInit;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


/**
 * TraceAutoConfiguration 类。
 * <p>该类型属于 链路追踪能力，负责根据 classpath、配置开关和缺省 Bean 条件装配 starter 默认能力。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(TraceProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.TRACE, name = PropertiesConstants.ENABLED, havingValue = "true")
public class TraceAutoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 链路追踪能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(TraceAutoConfiguration.class);

    /**
     * traceProperties 字段。
     * <p>用于保存 链路追踪能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final TraceProperties traceProperties;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param traceProperties traceProperties 参数，调用方应传入与 链路追踪能力 场景匹配的有效值
     */
    public TraceAutoConfiguration(TraceProperties traceProperties) {
        this.traceProperties = traceProperties;
    }

    /**
     * 注册 TLogPropertyInit 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     *
     * @param tLogIdGenerator tLogIdGenerator 参数，调用方应传入与 链路追踪能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Bean
    @Primary
    public TLogPropertyInit tLogPropertyInit(TLogIdGenerator tLogIdGenerator) {
        TLogProperties tLogProperties = traceProperties.getTlog();
        TLogPropertyInit tLogPropertyInit = new TLogPropertyInit();
        tLogPropertyInit.setPattern(tLogProperties.getPattern());
        tLogPropertyInit.setEnableInvokeTimePrint(tLogProperties.getEnableInvokeTimePrint());
        tLogPropertyInit.setMdcEnable(tLogProperties.getMdcEnable());
        // 设置自定义 TraceId 生成器
        TLogIdGeneratorLoader.setIdGenerator(tLogIdGenerator);
        return tLogPropertyInit;
    }

    /**
     * TLog 过滤器
     */
    @Bean
    public FilterRegistrationBean<TLogServletFilter> tLogServletFilter() {
        FilterRegistrationBean<TLogServletFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TLogServletFilter(traceProperties));
        registrationBean.setOrder(OrderedConstants.Filter.TRACE_FILTER);
        registrationBean.addUrlPatterns(StringConstants.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    /**
     * 自定义 Trace ID 生成器配置
     */
    @Bean
    @ConditionalOnMissingBean
    public TLogIdGenerator tLogIdGenerator() {
        return new TraceIdGenerator();
    }

    /**
     * 注册 void 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Trace' completed initialization.");
    }
}
