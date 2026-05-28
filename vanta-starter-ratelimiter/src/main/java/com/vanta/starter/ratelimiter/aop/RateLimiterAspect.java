package com.vanta.starter.ratelimiter.aop;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import com.vanta.starter.cache.redis.util.RedisUtils;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.core.util.expression.ExpressionUtils;
import com.vanta.starter.ratelimiter.annotation.RateLimiter;
import com.vanta.starter.ratelimiter.annotation.RateLimiters;
import com.vanta.starter.ratelimiter.autoconfigure.RateLimiterProperties;
import com.vanta.starter.ratelimiter.enums.LimitType;
import com.vanta.starter.ratelimiter.exception.RateLimiterException;
import com.vanta.starter.ratelimiter.generator.RateLimiterNameGenerator;
import com.vanta.starter.web.utils.ServletUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateLimiterConfig;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimiterAspect 类。
 * <p>该类型属于 限流能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@Aspect
public class RateLimiterAspect {

    /**
     * RATE_LIMITER_CACHE 字段。
     * <p>用于保存 限流能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final ConcurrentHashMap<String, RRateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();

    /**
     * properties 字段。
     * <p>用于保存 限流能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final RateLimiterProperties properties;

    /**
     * nameGenerator 字段。
     * <p>用于保存 限流能力 的资源命名配置，用于定位消息、索引、节点或业务对象。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final RateLimiterNameGenerator nameGenerator;

    /**
     * redissonClient 字段。
     * <p>用于保存 限流能力 的底层客户端或模板依赖，业务方可以通过自定义 Bean 替换。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final RedissonClient redissonClient;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param properties     properties 参数，调用方应传入与 限流能力 场景匹配的有效值
     * @param nameGenerator  nameGenerator 参数，调用方应传入与 限流能力 场景匹配的有效值
     * @param redissonClient redissonClient 参数，调用方应传入与 限流能力 场景匹配的有效值
     */
    public RateLimiterAspect(RateLimiterProperties properties,
                             RateLimiterNameGenerator nameGenerator,
                             RedissonClient redissonClient) {
        this.properties = properties;
        this.nameGenerator = nameGenerator;
        this.redissonClient = redissonClient;
    }

    /**
     * 单个限流注解切点
     */
    @Pointcut("@annotation(com.vanta.starter.ratelimiter.annotation.RateLimiter)")
    public void rateLimiterPointCut() {
    }

    /**
     * 多个限流注解切点
     */
    @Pointcut("@annotation(com.vanta.starter.ratelimiter.annotation.RateLimiters)")
    public void rateLimitersPointCut() {
    }

    /**
     * 单限流场景
     *
     * @param joinPoint   切点
     * @param rateLimiter 限流注解
     * @return 目标方法的执行结果
     * @throws Throwable /
     */
    @Around("@annotation(rateLimiter)")
    public Object aroundRateLimiter(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        if (isRateLimited(joinPoint, rateLimiter)) {
            throw new RateLimiterException(rateLimiter.message());
        }
        return joinPoint.proceed();
    }

    /**
     * 多限流场景
     *
     * @param joinPoint    切点
     * @param rateLimiters 限流组注解
     * @return 目标方法的执行结果
     * @throws Throwable /
     */
    @Around("@annotation(rateLimiters)")
    public Object aroundRateLimiters(ProceedingJoinPoint joinPoint, RateLimiters rateLimiters) throws Throwable {
        for (RateLimiter rateLimiter : rateLimiters.value()) {
            if (isRateLimited(joinPoint, rateLimiter)) {
                throw new RateLimiterException(rateLimiter.message());
            }
        }
        return joinPoint.proceed();
    }

    /**
     * 是否需要限流
     *
     * @param joinPoint   切点
     * @param rateLimiter 限流注解
     * @return true: 需要限流；false：不需要限流
     */
    private boolean isRateLimited(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        try {
            String cacheKey = this.getCacheKey(joinPoint, rateLimiter);
            RRateLimiter rRateLimiter = RATE_LIMITER_CACHE.computeIfAbsent(cacheKey, key -> redissonClient
                    .getRateLimiter(cacheKey));
            // 限流器配置
            RateType rateType = rateLimiter.type() == LimitType.CLUSTER ? RateType.PER_CLIENT : RateType.OVERALL;
            int rate = rateLimiter.rate();
            Duration rateInterval = Duration.ofMillis(rateLimiter.unit().toMillis(rateLimiter.interval()));
            // 判断是否需要更新限流器
            if (this.isConfigurationUpdateNeeded(rRateLimiter, rateType, rate, rateInterval)) {
                // 更新限流器
                rRateLimiter.setRate(rateType, rate, rateInterval);
            }
            // 尝试获取令牌
            return !rRateLimiter.tryAcquire();
        } catch (Exception e) {
            throw new RateLimiterException("服务器限流异常，请稍候再试", e);
        }
    }

    /**
     * 获取缓存 Key
     *
     * @param joinPoint   切点
     * @param rateLimiter 限流注解
     * @return 缓存 Key
     */
    private String getCacheKey(JoinPoint joinPoint, RateLimiter rateLimiter) {
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        // 获取名称
        String name = rateLimiter.name();
        if (CharSequenceUtil.isBlank(name)) {
            name = nameGenerator.generate(target, method, args);
        }
        // 解析 Key
        String key = rateLimiter.key();
        if (CharSequenceUtil.isNotBlank(key)) {
            Object eval = ExpressionUtils.eval(key, target, method, args);
            if (eval == null) {
                throw new RateLimiterException("限流 Key 解析错误");
            }
            key = Convert.toStr(eval);
        }
        // 获取后缀
        String suffix = switch (rateLimiter.type()) {
            case IP -> ServletUtils.getClientIP(ServletUtils.getRequest());
            case CLUSTER -> redissonClient.getId();
            default -> StringConstants.EMPTY;
        };
        return RedisUtils.formatKey(properties.getKeyPrefix(), name, key, suffix);
    }

    /**
     * 判断是否需要更新限流器配置
     *
     * @param rRateLimiter 限流器
     * @param rateType     限流类型（OVERALL：全局限流；PER_CLIENT：单机限流）
     * @param rate         速率（指定时间间隔产生的令牌数）
     * @param rateInterval 速率间隔
     * @return 是否需要更新配置
     */
    private boolean isConfigurationUpdateNeeded(RRateLimiter rRateLimiter,
                                                RateType rateType,
                                                long rate,
                                                Duration rateInterval) {
        RateLimiterConfig config = rRateLimiter.getConfig();
        return !Objects.equals(config.getRateType(), rateType) || !Objects.equals(config.getRate(), rate) || !Objects
                .equals(config.getRateInterval(), rateInterval.toMillis());
    }
}
