package com.vanta.starter.data.routing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Set;

@Aspect
public class RepositoryShardAspect {

    private final RepositoryShardDefinitionRegistry registry;

    private final Set<String> lookupKeys;

    public RepositoryShardAspect(RepositoryShardDefinitionRegistry registry) {
        this(registry, Set.of());
    }

    public RepositoryShardAspect(RepositoryShardDefinitionRegistry registry, Set<String> lookupKeys) {
        this.registry = registry;
        this.lookupKeys = lookupKeys == null ? Set.of() : lookupKeys;
    }

    @Around("within(*..repository.impl..*)")
    public Object routeRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        RepositoryShard shard = resolveShard(joinPoint);
        if (shard == null) {
            return joinPoint.proceed();
        }

        RoutingDataSourceContext.push(shard.value(), registry, lookupKeys);
        try {
            return joinPoint.proceed();
        } finally {
            RoutingDataSourceContext.pop();
        }
    }

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
