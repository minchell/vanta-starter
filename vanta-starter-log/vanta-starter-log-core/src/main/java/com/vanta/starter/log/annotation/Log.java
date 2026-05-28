package com.vanta.starter.log.annotation;

import com.vanta.starter.log.enums.Include;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 接口日志控制注解。
 *
 * <p>该注解可以放在 Controller 类或方法上，用于补充日志描述、所属模块、采集字段和忽略标记。
 * 方法级配置优先级高于类级配置，适合对少数接口做差异化日志采集。</p>
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /**
     * 日志描述。
     *
     * <p>
     * 优先级：@Log("描述") > @Operation(summary="描述")
     * </p>
     */
    String value() default "";

    /**
     * 所属业务模块。
     *
     * <p>
     * 优先级：接口方法上的 @Log(module = "模块") > 接口类上的 @Log(module = "模块") > @Tag(name = "模块") 内容
     * </p>
     */
    String module() default "";

    /**
     * 在全局配置基础上额外采集的字段。
     *
     * @return 需要额外采集的日志字段
     */
    Include[] includes() default {};

    /**
     * 在全局配置基础上排除的字段。
     *
     * @return 不需要采集的日志字段
     */
    Include[] excludes() default {};

    /**
     * 是否忽略日志记录。
     *
     * <p>方法级 ignore 可以覆盖类级日志配置，用于跳过敏感接口、健康检查或无价值接口。</p>
     *
     * @return true 表示跳过日志记录，false 表示按全局和注解配置记录日志
     */
    boolean ignore() default false;
}
