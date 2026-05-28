package com.vanta.starter.core.util.expression;

import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 表达式解析工具类。
 * <p>
 * 该工具类提供安全的表达式求值入口：空表达式直接返回 {@code null}，解析或执行异常会记录日志并返回 {@code null}。
 * 适合注解属性、缓存 key、幂等 key、日志模板等需要从方法调用上下文中动态取值的场景。
 * </p>
 */
public class ExpressionUtils {

    /**
     * 当前工具类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(ExpressionUtils.class);

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private ExpressionUtils() {
    }

    /**
     * 解析并执行表达式。
     * <p>
     * 表达式执行时，目标方法参数会按参数名注册为 SpEL 变量，目标方法、参数和目标对象会放入 root object。
     * 执行失败时返回 {@code null}，由调用方决定是否降级或继续执行。
     * </p>
     *
     * @param script 表达式脚本。
     * @param target 目标对象。
     * @param method 目标方法。
     * @param args   方法参数。
     * @return 表达式解析结果；表达式为空或执行失败时返回 {@code null}。
     */
    public static Object eval(String script, Object target, Method method, Object... args) {
        try {
            if (CharSequenceUtil.isBlank(script)) {
                return null;
            }
            ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(script, method);
            ExpressionInvokeContext invokeContext = new ExpressionInvokeContext(method, args, target);
            return expressionEvaluator.apply(invokeContext);
        } catch (Exception e) {
            log.error("Error occurs when eval script \"{}\" in {} : {}", script, method, e.getMessage(), e);
            return null;
        }
    }
}
