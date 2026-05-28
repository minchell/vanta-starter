package com.vanta.starter.core.util.expression;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Spring EL 表达式求值器。
 * <p>
 * 该类负责把字符串表达式编译为 Spring {@link Expression}，并在执行时把目标方法参数注册为 SpEL 变量。
 * 它是 {@link ExpressionEvaluator} 的默认实现，不直接访问外部资源，所有输入都来自调用方提供的上下文。
 * </p>
 */
public class SpelEvaluator implements Function<Object, Object> {

    /**
     * SpEL 表达式解析器。
     */
    private static final ExpressionParser PARSER;

    /**
     * 方法参数名发现器。
     * <p>
     * 依赖编译产物中的参数名信息或调试信息，无法发现参数名时会返回 {@code null}。
     * </p>
     */
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER;

    static {
        PARSER = new SpelExpressionParser();
        PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    }

    /**
     * 已解析的 SpEL 表达式。
     */
    private final Expression expression;

    /**
     * 目标方法参数名数组。
     */
    private String[] parameterNames;

    /**
     * 创建 SpEL 表达式求值器。
     *
     * @param script       SpEL 表达式脚本。
     * @param defineMethod 表达式绑定的目标方法，用于解析参数名。
     */
    public SpelEvaluator(String script, Method defineMethod) {
        expression = PARSER.parseExpression(script);
        if (defineMethod.getParameterCount() > 0) {
            parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(defineMethod);
        }
    }

    /**
     * 执行 SpEL 表达式。
     * <p>
     * 当存在方法参数名时，会把每个参数按名称写入表达式上下文变量，表达式可以通过 {@code #参数名} 访问。
     * </p>
     *
     * @param rootObject 表达式根对象，必须是 {@link ExpressionInvokeContext}。
     * @return 表达式计算结果。
     */
    @Override
    public Object apply(Object rootObject) {
        EvaluationContext context = new StandardEvaluationContext(rootObject);
        ExpressionInvokeContext invokeContext = (ExpressionInvokeContext) rootObject;
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], invokeContext.getArgs()[i]);
            }
        }
        return expression.getValue(context);
    }
}
