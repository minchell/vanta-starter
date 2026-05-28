package com.vanta.starter.data.autoconfigure;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.util.GeneralPropertySourceFactory;
import com.vanta.starter.data.autoconfigure.idgenerator.MyBatisPlusIdGeneratorConfiguration;
import com.vanta.starter.data.handler.CompositeBaseEnumTypeHandler;
import com.vanta.starter.data.handler.InjMetaObjectHandler;
import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

/**
 * MyBatis-Plus 自动配置。
 * <p>
 * 该配置在 {@code mybatis-plus.extension.enabled=true} 时启用，负责 Mapper 扫描、枚举类型处理器、
 * 分页/乐观锁/防全表更新插件、自动填充处理器以及 ID 生成器配置。
 * </p>
 */
@AutoConfiguration
@MapperScan("${mybatis-plus.extension.mapper-package}")
@EnableTransactionManagement(proxyTargetClass = true)
@EnableConfigurationProperties(MyBatisPlusExtensionProperties.class)
@ConditionalOnProperty(prefix = "mybatis-plus.extension", name = PropertiesConstants.ENABLED, havingValue = "true")
@PropertySource(value = "classpath:default-data-mybatis-plus.yml", factory = GeneralPropertySourceFactory.class)
public class MybatisPlusAutoConfiguration {

    /**
     * 当前自动配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(MybatisPlusAutoConfiguration.class);

    /**
     * MyBatis Plus 配置
     *
     * @return MyBatis-Plus 配置定制器。
     */
    @Bean
    public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
        return properties -> properties.getConfiguration().setDefaultEnumTypeHandler(CompositeBaseEnumTypeHandler.class);
    }

    /**
     * 创建 MyBatis-Plus 插件拦截器。
     *
     * @param properties MyBatis-Plus 扩展配置属性。
     * @return MyBatis-Plus 插件拦截器。
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MyBatisPlusExtensionProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 其他拦截器
        Map<String, InnerInterceptor> innerInterceptors = SpringUtil.getBeansOfType(InnerInterceptor.class);
        if (!innerInterceptors.isEmpty()) {
            innerInterceptors.values().forEach(interceptor::addInnerInterceptor);
        }
        // 分页插件
        MyBatisPlusExtensionProperties.PaginationProperties paginationProperties = properties.getPagination();
        if (paginationProperties != null && paginationProperties.isEnabled()) {
            interceptor.addInnerInterceptor(this.paginationInnerInterceptor(paginationProperties));
        }
        // 乐观锁插件
        if (properties.isOptimisticLockerEnabled()) {
            interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        }
        // 防全表更新与删除插件
        if (properties.isBlockAttackPluginEnabled()) {
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        }
        return interceptor;
    }

    /**
     * 创建 MyBatis-Plus 元对象自动填充处理器。
     *
     * @return 元对象自动填充处理器。
     */
    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaObjectHandler() {
        return new InjMetaObjectHandler();
    }

    /**
     * 分页插件配置（<a href="https://baomidou.com/pages/97710a/#paginationinnerinterceptor">PaginationInnerInterceptor</a>）
     *
     * @param paginationProperties 分页插件配置属性。
     * @return 分页内部拦截器。
     */
    private PaginationInnerInterceptor paginationInnerInterceptor(MyBatisPlusExtensionProperties.PaginationProperties paginationProperties) {
        // 对于单一数据库类型来说，都建议配置该值，避免每次分页都去抓取数据库类型
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(paginationProperties
                .getDbType());
        paginationInnerInterceptor.setOverflow(paginationProperties.isOverflow());
        paginationInnerInterceptor.setMaxLimit(paginationProperties.getMaxLimit());
        return paginationInnerInterceptor;
    }

    /**
     * 输出 MyBatis-Plus 自动配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'MyBatis Plus' completed initialization.");
    }

    /**
     * ID 生成器自动配置组合。
     * <p>
     * 按配置导入默认、CosId 和自定义三类 ID 生成器条件配置。
     * </p>
     */
    @Configuration
    @Import({MyBatisPlusIdGeneratorConfiguration.Default.class, MyBatisPlusIdGeneratorConfiguration.CosId.class, MyBatisPlusIdGeneratorConfiguration.Custom.class})
    protected static class MyBatisPlusIdGeneratorAutoConfiguration {

    }
}
