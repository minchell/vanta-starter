package com.vanta.starter.core.util.expression;

import java.lang.reflect.Method;

/**
 * 表达式调用上下文。
 * <p>
 * 该对象承载一次方法调用相关的目标方法、参数数组和目标对象，供 SpEL 表达式读取。
 * 表达式求值时会把该对象作为 root object，同时把方法参数名注册为表达式变量。
 * </p>
 */
public class ExpressionInvokeContext {

    /**
     * 目标方法
     */
    private Method method;

    /**
     * 方法参数数组
     */
    private Object[] args;

    /**
     * 目标对象
     */
    private Object target;

    /**
     * 创建表达式调用上下文。
     *
     * @param method 目标方法。
     * @param args   方法参数数组。
     * @param target 目标对象。
     */
    public ExpressionInvokeContext(Method method, Object[] args, Object target) {
        this.method = method;
        this.args = args;
        this.target = target;
    }

    /**
     * 获取目标方法。
     *
     * @return 目标方法
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 设置目标方法。
     *
     * @param method 目标方法
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * 获取方法参数数组。
     *
     * @return 方法参数数组
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 设置方法参数数组。
     *
     * @param args 方法参数数组
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * 获取目标对象。
     *
     * @return 目标对象
     */
    public Object getTarget() {
        return target;
    }

    /**
     * 设置目标对象。
     *
     * @param target 目标对象
     */
    public void setTarget(Object target) {
        this.target = target;
    }
}
