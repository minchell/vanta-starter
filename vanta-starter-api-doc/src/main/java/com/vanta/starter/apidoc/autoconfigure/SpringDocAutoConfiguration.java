package com.vanta.starter.apidoc.autoconfigure;

import com.vanta.starter.apidoc.processor.BaseEnumProcessor;
import com.vanta.starter.core.autoconfigure.application.ApplicationProperties;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.util.GeneralPropertySourceFactory;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * SpringDocAutoConfiguration 类。
 * <p>该类型属于 接口文档能力，负责根据 classpath、配置开关和缺省 Bean 条件装配 starter 默认能力。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@AutoConfiguration(before = SpringDocConfiguration.class)
@PropertySource(value = "classpath:default-api-doc.yml", factory = GeneralPropertySourceFactory.class)
@ConditionalOnProperty(prefix = PropertiesConstants.API_DOC, name = PropertiesConstants.ENABLED, havingValue = "true")
public class SpringDocAutoConfiguration implements WebMvcConfigurer {

    /**
     * log 字段。
     * <p>用于保存 接口文档能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(SpringDocAutoConfiguration.class);

    /**
     * 执行 addResourceHandlers 逻辑。
     * 该方法属于 接口文档能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param registry registry 参数，调用方应传入与 接口文档能力 场景匹配的有效值
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/");
    }

    /**
     * Open API 配置
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openApi(ApplicationProperties applicationProperties) {
        Info info = new Info().title("%s %s".formatted(applicationProperties.getName(), "Doc"))
                .version(applicationProperties.getVersion())
                .description(applicationProperties.getDescription());
        ApplicationProperties.Contact contact = applicationProperties.getContact();
        if (contact != null) {
            info.contact(new Contact().name(contact.getName()).email(contact.getEmail()).url(contact.getUrl()));
        }
        ApplicationProperties.License license = applicationProperties.getLicense();
        if (license != null) {
            info.license(new License().name(license.getName()).url(license.getUrl()));
        }
        OpenAPI openApi = new OpenAPI();
        openApi.info(info);
        return openApi;
    }

    /**
     * BaseEnum 枚举处理器
     *
     * @return {@link BaseEnumProcessor }
     */
    @Bean
    @ConditionalOnMissingBean
    public BaseEnumProcessor baseEnumProcessor() {
        return new BaseEnumProcessor();
    }

    /**
     * 注册 void 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'ApiDoc' completed initialization.");
    }
}
