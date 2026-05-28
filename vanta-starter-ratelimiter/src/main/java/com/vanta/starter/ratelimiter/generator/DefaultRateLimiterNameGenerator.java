package com.vanta.starter.ratelimiter.generator;

import cn.hutool.core.util.ClassUtil;
import com.vanta.starter.core.constant.StringConstants;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;


/**
 * DefaultRateLimiterNameGenerator 类。
 * <p>该类型属于 限流能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class DefaultRateLimiterNameGenerator implements RateLimiterNameGenerator {

    /**
     * nameMap 字段。
     * <p>用于保存 限流能力 的资源命名配置，用于定位消息、索引、节点或业务对象。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    protected final ConcurrentHashMap<Method, String> nameMap = new ConcurrentHashMap<>();

    /**
     * 执行 generate 逻辑。
     * 该方法属于 限流能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param target target 参数，调用方应传入与 限流能力 场景匹配的有效值
     * @param method method 参数，调用方应传入与 限流能力 场景匹配的有效值
     * @param args   args 参数，调用方应传入与 限流能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String generate(Object target, Method method, Object... args) {
        return nameMap.computeIfAbsent(method, key -> {
            final StringBuilder nameSb = new StringBuilder();
            String className = method.getDeclaringClass().getName();
            nameSb.append(ClassUtil.getShortClassName(className));
            nameSb.append(StringConstants.DOT);
            nameSb.append(method.getName());
            nameSb.append(StringConstants.ROUND_BRACKET_START);
            for (Class<?> clazz : method.getParameterTypes()) {
                this.getDescriptor(nameSb, clazz);
            }
            nameSb.append(StringConstants.ROUND_BRACKET_END);
            return nameSb.toString();
        });
    }

    /**
     * 获取指定数据类型的描述
     *
     * @param sb        名称字符串缓存
     * @param typeClass 数据类型
     */
    private void getDescriptor(final StringBuilder sb, final Class<?> typeClass) {
        Class<?> clazz = typeClass;
        while (true) {
            if (clazz.isPrimitive()) {
                sb.append(this.getPrimitiveChar(clazz));
                return;
            } else if (clazz.isArray()) {
                sb.append(StringConstants.BRACKET_START);
                clazz = clazz.getComponentType();
            } else {
                sb.append('L');
                String name = clazz.getName();
                name = ClassUtil.getShortClassName(name);
                sb.append(name);
                sb.append(StringConstants.SEMICOLON);
                return;
            }
        }
    }

    /**
     * 根据基本数据获取类型字符
     *
     * @param clazz 数据类型
     * @return 类型字符
     */
    private char getPrimitiveChar(Class<?> clazz) {
        char c;
        if (clazz == Integer.TYPE) {
            c = 'I';
        } else if (clazz == Void.TYPE) {
            c = 'V';
        } else if (clazz == Boolean.TYPE) {
            c = 'Z';
        } else if (clazz == Byte.TYPE) {
            c = 'B';
        } else if (clazz == Character.TYPE) {
            c = 'C';
        } else if (clazz == Short.TYPE) {
            c = 'S';
        } else if (clazz == Double.TYPE) {
            c = 'D';
        } else if (clazz == Float.TYPE) {
            c = 'F';
        } else {
            c = 'J';
        }
        return c;
    }
}
