package com.vanta.starter.data.routing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * Repository 数据源定位切面。
 * <p>
 * 该切面只负责解析 Repository 实现类或方法上的 {@link RepositoryShard}，
 * 并在调用期间设置数据源路由上下文。调用结束后一定清理当前层级上下文。
 * </p>
 */
@Aspect
public class RepositoryShardAspect {

    /**
     * 拦截 Repository 实现。
     *
     * @param joinPoint 连接点。
     * @return 原方法返回值。
     * @throws Throwable 原方法异常。
     */
    @Around("within(*..repository.impl..*)")
    public Object routeRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        RepositoryShard shard = resolveShard(joinPoint);

        if (shard == null) {
            try {
                return joinPoint.proceed();
            } finally {
                RoutingDataSourceContext.clear();
            }
        }

        RoutingDataSourceContext.push(shard.value());

        try {
            return joinPoint.proceed();
        } finally {
            RoutingDataSourceContext.pop();
            RoutingDataSourceContext.current().ifPresentOrElse(ignored -> {
                // ignored nothing
            }, RoutingDataSourceContext::clear);
        }
    }

    /**
     * 解析方法或类上的数据源定位注解。
     *
     * @param joinPoint 连接点。
     * @return 数据源定位注解。
     */
    private RepositoryShard resolveShard(ProceedingJoinPoint joinPoint) {
        if (!(joinPoint.getSignature() instanceof MethodSignature signature)) {
            return null;
        }

        Method method = signature.getMethod();
        Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());

        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        RepositoryShard methodShard = AnnotatedElementUtils.findMergedAnnotation(specificMethod, RepositoryShard.class);
        if (methodShard != null) {
            return methodShard;
        }

        return AnnotatedElementUtils.findMergedAnnotation(targetClass, RepositoryShard.class);
    }

}
