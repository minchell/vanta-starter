package com.vanta.starter.web.annotation;

import com.vanta.starter.web.autoconfigure.response.GlobalResponseAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 Vanta 全局响应包装能力。
 * <p>
 * 标注在 Spring Boot 启动类或配置类上后，会导入 {@link GlobalResponseAutoConfiguration}，
 * 注册 graceful-response 所需的响应包装、异常处理和状态工厂 Bean。
 * </p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({GlobalResponseAutoConfiguration.class})
public @interface EnableGlobalResponse {
}
