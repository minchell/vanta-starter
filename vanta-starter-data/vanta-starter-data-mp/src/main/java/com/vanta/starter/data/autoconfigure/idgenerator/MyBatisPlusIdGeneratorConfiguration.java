package com.vanta.starter.data.autoconfigure.idgenerator;

import cn.hutool.core.net.NetUtil;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import me.ahoo.cosid.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;

/**
 * MyBatis-Plus ID 生成器条件配置。
 * <p>
 * 该配置根据 {@code mybatis-plus.extension.id-generator.type} 选择默认雪花、CosId 或业务自定义生成器。
 * 默认分支使用 MyBatis-Plus {@link DefaultIdentifierGenerator}，不会依赖外部中间件。
 * </p>
 */
public class MyBatisPlusIdGeneratorConfiguration {

    /**
     * 当前配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(MyBatisPlusIdGeneratorConfiguration.class);

    /**
     * 私有构造方法。
     * <p>
     * 条件配置类只作为 Spring 配置命名空间使用，不允许实例化。
     * </p>
     */
    private MyBatisPlusIdGeneratorConfiguration() {
    }

    /**
     * 自定义 ID 生成器-默认（雪花算法，使用网卡信息绑定雪花生成器，防止集群雪花 ID 重复）
     */
    @ConditionalOnMissingBean(IdentifierGenerator.class)
    @ConditionalOnProperty(name = "mybatis-plus.extension.id-generator.type", havingValue = "default", matchIfMissing = true)
    public static class Default {
        static {
            log.debug("[Vanta Starter] - Auto Configuration 'MyBatis Plus-IdGenerator-Default' completed initialization.");
        }

        /**
         * 创建默认 MyBatis-Plus ID 生成器。
         *
         * @return 基于本机信息初始化的默认雪花 ID 生成器。
         */
        @Bean
        public IdentifierGenerator identifierGenerator() {
            return new DefaultIdentifierGenerator(NetUtil.getLocalhost());
        }
    }

    /**
     * 自定义 ID 生成器-CosId
     */
    @ConditionalOnMissingBean(IdentifierGenerator.class)
    @ConditionalOnClass(IdGenerator.class)
    @ConditionalOnProperty(name = "mybatis-plus.extension.id-generator.type", havingValue = "cosid")
    public static class CosId {
        static {
            log.debug("[Vanta Starter] - Auto Configuration 'MyBatis Plus-IdGenerator-CosId' completed initialization.");
        }

        /**
         * 创建 CosId 适配的 MyBatis-Plus ID 生成器。
         *
         * @return 使用 CosId SnowflakeId 的 MyBatis-Plus ID 生成器。
         */
        @Bean
        public IdentifierGenerator identifierGenerator() {
            return new MyBatisPlusCosIdIdentifierGenerator();
        }
    }

    /**
     * 自定义 ID 生成器
     */
    @ConditionalOnProperty(name = "mybatis-plus.extension.id-generator.type", havingValue = "custom")
    public static class Custom {
        /**
         * 提示业务方提供自定义 ID 生成器。
         * <p>
         * 当配置为 custom 但容器中没有 {@link IdentifierGenerator} Bean 时，该方法会抛出异常，避免静默退回默认策略。
         * </p>
         *
         * @return 业务方自定义 ID 生成器。
         */
        @Bean
        @ConditionalOnMissingBean
        public IdentifierGenerator identifierGenerator() {
            if (log.isErrorEnabled()) {
                log.error("Consider defining a bean of type '{}' in your configuration.", ResolvableType
                        .forClass(IdentifierGenerator.class));
            }
            throw new NoSuchBeanDefinitionException(IdentifierGenerator.class);
        }
    }
}
