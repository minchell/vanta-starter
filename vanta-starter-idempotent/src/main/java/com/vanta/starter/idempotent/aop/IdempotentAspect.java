package com.vanta.starter.idempotent.aop;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import com.vanta.starter.cache.redis.util.RedisUtils;
import com.vanta.starter.core.util.expression.ExpressionUtils;
import com.vanta.starter.idempotent.annotation.Idempotent;
import com.vanta.starter.idempotent.autoconfigure.IdempotentProperties;
import com.vanta.starter.idempotent.exception.IdempotentException;
import com.vanta.starter.idempotent.generator.IdempotentNameGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * IdempotentAspect 类。
 * <p>该类型属于 幂等能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@Aspect
public class IdempotentAspect {

    /**
     * properties 字段。
     * <p>用于保存 幂等能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final IdempotentProperties properties;
    /**
     * nameGenerator 字段。
     * <p>用于保存 幂等能力 的资源命名配置，用于定位消息、索引、节点或业务对象。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final IdempotentNameGenerator nameGenerator;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param properties    properties 参数，调用方应传入与 幂等能力 场景匹配的有效值
     * @param nameGenerator nameGenerator 参数，调用方应传入与 幂等能力 场景匹配的有效值
     */
    public IdempotentAspect(IdempotentProperties properties, IdempotentNameGenerator nameGenerator) {
        this.properties = properties;
        this.nameGenerator = nameGenerator;
    }

    /**
     * 幂等处理
     *
     * @param joinPoint  切点
     * @param idempotent 幂等注解
     * @return 目标方法的执行结果
     * @throws Throwable /
     */
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String cacheKey = this.getCacheKey(joinPoint, idempotent);

        // 如果键已存在，则抛出异常
        if (!RedisUtils.setIfAbsent(cacheKey, cacheKey, Duration.ofMillis(idempotent.unit()
                .toMillis(idempotent.timeout())))) {
            throw new IdempotentException(idempotent.message());
        }

        // 执行目标方法
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            // 删除键
            RedisUtils.delete(cacheKey);
            throw e;
        }
    }

    /**
     * 获取缓存 Key
     *
     * @param joinPoint  切点
     * @param idempotent 幂等注解
     * @return 缓存 Key
     */
    private String getCacheKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        // 获取名称
        String name = idempotent.name();
        if (CharSequenceUtil.isBlank(name)) {
            name = nameGenerator.generate(target, method, args);
        }
        // 解析 Key
        String key = idempotent.key();
        if (CharSequenceUtil.isNotBlank(key)) {
            Object eval = ExpressionUtils.eval(key, target, method, args);
            if (eval == null) {
                throw new IdempotentException("幂等 Key 解析错误");
            }
            key = Convert.toStr(eval);
        }
        return RedisUtils.formatKey(properties.getKeyPrefix(), name, key);
    }
}
