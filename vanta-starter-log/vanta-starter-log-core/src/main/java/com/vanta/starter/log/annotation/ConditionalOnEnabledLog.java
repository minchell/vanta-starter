package com.vanta.starter.log.annotation;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志能力启用条件注解。
 * <p>
 * 自动配置类或 Bean 方法标注该注解后，只有在 {@code vanta.log.enabled=true} 或未显式配置该开关时才会生效。
 * 该注解让日志 starter 具备统一的配置开关，业务项目可以通过一行配置完整关闭日志能力。
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ConditionalOnProperty(prefix = PropertiesConstants.LOG, name = PropertiesConstants.ENABLED, havingValue = "true")
public @interface ConditionalOnEnabledLog {
}
