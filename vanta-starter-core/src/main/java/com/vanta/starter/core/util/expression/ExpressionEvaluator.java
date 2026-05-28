package com.vanta.starter.core.util.expression;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 表达式求值器统一入口。
 * <p>
 * 该类对外暴露标准 {@link Function} 接口，内部默认使用 {@link SpelEvaluator} 完成 Spring EL 表达式解析和执行。
 * 这样调用方只需要关心表达式输入和返回值，不需要直接依赖具体表达式引擎实现。
 * </p>
 */
public class ExpressionEvaluator implements Function<Object, Object> {

    /**
     * 实际执行表达式求值的函数。
     */
    private final Function<Object, Object> evaluator;

    /**
     * 创建表达式求值器。
     *
     * @param script       表达式脚本。
     * @param defineMethod 表达式所在的目标方法，用于解析方法参数名。
     */
    public ExpressionEvaluator(String script, Method defineMethod) {
        this.evaluator = new SpelEvaluator(script, defineMethod);
    }

    /**
     * 执行表达式求值。
     *
     * @param rootObject 表达式根对象，通常是 {@link ExpressionInvokeContext}。
     * @return 表达式计算结果。
     */
    @Override
    public Object apply(Object rootObject) {
        return evaluator.apply(rootObject);
    }

    /**
     * 获取实际表达式求值函数。
     *
     * @return 表达式求值函数。
     */
    Function<Object, Object> getEvaluator() {
        return evaluator;
    }
}
