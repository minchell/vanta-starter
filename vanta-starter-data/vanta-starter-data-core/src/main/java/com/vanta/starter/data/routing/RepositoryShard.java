package com.vanta.starter.data.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Repository 数据源定位注解。
 * <p>
 * 该注解只能用于 Repository 实现类或实现方法。Controller、Application Service、
 * Domain Service 不允许感知数据库位置，也不允许使用该注解。
 * </p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RepositoryShard {

    /**
     * 数据源定位类型。
     *
     * @return 数据源定位类型。
     */
    RepositoryShardType value() default RepositoryShardType.AUTO;
}
