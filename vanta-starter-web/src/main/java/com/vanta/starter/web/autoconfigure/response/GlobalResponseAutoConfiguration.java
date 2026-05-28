package com.vanta.starter.web.autoconfigure.response;

import com.feiniaojin.gracefulresponse.ExceptionAliasRegister;
import com.feiniaojin.gracefulresponse.advice.AdviceSupport;
import com.feiniaojin.gracefulresponse.advice.DataExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.DefaultGlobalExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.DefaultRejectStrategyImpl;
import com.feiniaojin.gracefulresponse.advice.DefaultValidationExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.FrameworkExceptionAdvice;
import com.feiniaojin.gracefulresponse.advice.GrI18nResponseBodyAdvice;
import com.feiniaojin.gracefulresponse.advice.GrNotVoidResponseBodyAdvice;
import com.feiniaojin.gracefulresponse.advice.GrVoidResponseBodyAdvice;
import com.feiniaojin.gracefulresponse.advice.ReleaseExceptionHandlerExceptionResolver;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.BeforeControllerAdviceProcess;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.ControllerAdvicePredicate;
import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.RejectStrategy;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseFactory;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatusFactoryImpl;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.util.GeneralPropertySourceFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 全局响应包装自动配置。
 * <p>
 * 该配置整合 graceful-response 的响应体包装、异常处理、国际化和响应状态工厂。
 * 核心 Bean 多数使用 {@link ConditionalOnMissingBean}，业务方可以通过自定义同类型 Bean 替换默认实现。
 * </p>
 */
@AutoConfiguration
@EnableConfigurationProperties(GlobalResponseProperties.class)
@PropertySource(value = "classpath:default-response.yml", factory = GeneralPropertySourceFactory.class)
@ConditionalOnProperty(prefix = PropertiesConstants.WEB_RESPONSE, name = PropertiesConstants.ENABLED, havingValue = "true")
public class GlobalResponseAutoConfiguration {

    /**
     * 当前自动配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalResponseAutoConfiguration.class);

    /**
     * 全局响应配置属性。
     */
    private final GlobalResponseProperties globalResponseProperties;

    /**
     * 创建全局响应自动配置。
     *
     * @param globalResponseProperties 全局响应配置属性。
     */
    public GlobalResponseAutoConfiguration(GlobalResponseProperties globalResponseProperties) {
        this.globalResponseProperties = globalResponseProperties;
    }

    /**
     * 创建非 void 返回值的全局响应体处理器。
     *
     * @return 非 void 返回值响应体处理器。
     */
    @Bean
    @ConditionalOnMissingBean
    public GrNotVoidResponseBodyAdvice grNotVoidResponseBodyAdvice() {
        return new GrNotVoidResponseBodyAdvice();
    }

    /**
     * 创建 void 返回值的全局响应体处理器。
     *
     * @return void 返回值响应体处理器。
     */
    @Bean
    @ConditionalOnMissingBean
    public GrVoidResponseBodyAdvice grVoidResponseBodyAdvice() {
        return new GrVoidResponseBodyAdvice();
    }

    /**
     * 创建异常处理前置回调。
     *
     * @return 异常处理前置回调，默认根据配置打印异常日志。
     */
    @Bean
    @ConditionalOnMissingBean
    public BeforeControllerAdviceProcess beforeControllerAdviceProcess() {
        return new DefaultBeforeControllerAdviceProcessImpl(globalResponseProperties);
    }

    /**
     * 创建框架异常处理器。
     *
     * @param beforeControllerAdviceProcess 异常处理前置回调。
     * @param rejectStrategy                响应包装拒绝策略。
     * @return 框架异常处理器。
     */
    @Bean
    public FrameworkExceptionAdvice frameworkExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                             @Lazy RejectStrategy rejectStrategy) {
        FrameworkExceptionAdvice frameworkExceptionAdvice = new FrameworkExceptionAdvice();
        frameworkExceptionAdvice.setRejectStrategy(rejectStrategy);
        frameworkExceptionAdvice.setControllerAdviceProcessor(frameworkExceptionAdvice);
        frameworkExceptionAdvice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        frameworkExceptionAdvice.setControllerAdviceHttpProcessor(frameworkExceptionAdvice);
        return frameworkExceptionAdvice;
    }

    /**
     * 创建数据校验异常处理器。
     *
     * @param beforeControllerAdviceProcess 异常处理前置回调。
     * @param rejectStrategy                响应包装拒绝策略。
     * @return 数据校验异常处理器。
     */
    @Bean
    public DataExceptionAdvice dataExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                   @Lazy RejectStrategy rejectStrategy) {
        DataExceptionAdvice dataExceptionAdvice = new DataExceptionAdvice();
        dataExceptionAdvice.setRejectStrategy(rejectStrategy);
        dataExceptionAdvice.setControllerAdviceProcessor(dataExceptionAdvice);
        dataExceptionAdvice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        dataExceptionAdvice.setControllerAdviceHttpProcessor(dataExceptionAdvice);
        return dataExceptionAdvice;
    }

    /**
     * 创建默认全局异常处理器。
     *
     * @param beforeControllerAdviceProcess 异常处理前置回调。
     * @param rejectStrategy                响应包装拒绝策略。
     * @return 默认全局异常处理器。
     */
    @Bean
    public DefaultGlobalExceptionAdvice defaultGlobalExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                                     @Lazy RejectStrategy rejectStrategy) {
        DefaultGlobalExceptionAdvice advice = new DefaultGlobalExceptionAdvice();
        advice.setRejectStrategy(rejectStrategy);
        CopyOnWriteArrayList<ControllerAdvicePredicate> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(advice);
        advice.setPredicates(copyOnWriteArrayList);
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }

    /**
     * 创建默认参数校验异常处理器。
     *
     * @param beforeControllerAdviceProcess 异常处理前置回调。
     * @param rejectStrategy                响应包装拒绝策略。
     * @return 默认参数校验异常处理器。
     */
    @Bean
    public DefaultValidationExceptionAdvice defaultValidationExceptionAdvice(BeforeControllerAdviceProcess beforeControllerAdviceProcess,
                                                                             @Lazy RejectStrategy rejectStrategy) {
        DefaultValidationExceptionAdvice advice = new DefaultValidationExceptionAdvice();
        advice.setRejectStrategy(rejectStrategy);
        advice.setControllerAdviceProcessor(advice);
        advice.setBeforeControllerAdviceProcess(beforeControllerAdviceProcess);
        // 设置默认参数校验异常http处理器
        advice.setControllerAdviceHttpProcessor(advice);
        return advice;
    }

    /**
     * 创建响应包装拒绝策略。
     *
     * @return graceful-response 默认拒绝策略。
     */
    @Bean
    public RejectStrategy rejectStrategy() {
        return new DefaultRejectStrategyImpl();
    }

    /**
     * 创建用于释放异常处理器的解析器。
     *
     * @return 异常处理器解析器。
     */
    @Bean
    public ExceptionHandlerExceptionResolver releaseExceptionHandlerExceptionResolver() {
        return new ReleaseExceptionHandlerExceptionResolver();
    }

    /**
     * 创建响应体国际化处理器。
     *
     * @return graceful-response 国际化响应体处理器。
     */
    @Bean
    @ConditionalOnProperty(prefix = PropertiesConstants.WEB_RESPONSE, name = "i18n", havingValue = "true")
    public GrI18nResponseBodyAdvice grI18nResponseBodyAdvice() {
        return new GrI18nResponseBodyAdvice();
    }

    /**
     * 创建国际化消息源。
     *
     * @return 默认读取 {@code i18n} 和 {@code i18n/messages} 资源包的消息源。
     */
    @Bean
    @ConditionalOnProperty(prefix = PropertiesConstants.WEB_RESPONSE, name = "i18n", havingValue = "true")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n", "i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.CHINA);
        return messageSource;
    }

    /**
     * 创建响应对象工厂。
     *
     * @return graceful-response 默认响应工厂。
     */
    @Bean
    @ConditionalOnMissingBean
    public ResponseFactory responseBeanFactory() {
        return new DefaultResponseFactory();
    }

    /**
     * 创建响应状态工厂。
     *
     * @return graceful-response 默认响应状态工厂。
     */
    @Bean
    @ConditionalOnMissingBean
    public ResponseStatusFactory responseStatusFactory() {
        return new DefaultResponseStatusFactoryImpl();
    }

    /**
     * 创建异常别名注册器。
     *
     * @return 异常别名注册器。
     */
    @Bean
    public ExceptionAliasRegister exceptionAliasRegister() {
        return new ExceptionAliasRegister();
    }

    /**
     * 创建响应处理支持组件。
     *
     * @return graceful-response 响应支持组件。
     */
    @Bean
    public AdviceSupport adviceSupport() {
        return new AdviceSupport();
    }

    /**
     * 输出全局响应自动配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Web-Global Response' completed initialization.");
    }
}
