package com.vanta.starter.idempotent.generator;

import java.lang.reflect.Method;


/**
 * IdempotentNameGenerator 接口。
 * <p>该类型属于 幂等能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public interface IdempotentNameGenerator {

    /**
     * 生成幂等名称
     *
     * @param target 目标实例
     * @param method 目标方法
     * @param args   方法参数
     * @return 幂等名称
     */
    String generate(Object target, Method method, Object... args);
}