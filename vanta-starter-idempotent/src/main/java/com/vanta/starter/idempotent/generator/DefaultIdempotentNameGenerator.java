package com.vanta.starter.idempotent.generator;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.vanta.starter.core.constant.StringConstants;

import java.lang.reflect.Method;


/**
 * DefaultIdempotentNameGenerator 类。
 * <p>该类型属于 幂等能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class DefaultIdempotentNameGenerator implements IdempotentNameGenerator {

    /**
     * 执行 generate 逻辑。
     * 该方法属于 幂等能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param target target 参数，调用方应传入与 幂等能力 场景匹配的有效值
     * @param method method 参数，调用方应传入与 幂等能力 场景匹配的有效值
     * @param args   args 参数，调用方应传入与 幂等能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String generate(Object target, Method method, Object... args) {
        StringBuilder nameSb = new StringBuilder();
        // 添加类名
        nameSb.append(ClassUtil.getClassName(target, false));
        nameSb.append(StringConstants.COLON);
        // 添加方法名
        nameSb.append(method.getName());
        // 添加参数信息的哈希值（如果有参数）
        if (args != null && args.length > 0) {
            nameSb.append(StringConstants.COLON);
            // 使用JSONUtil序列化参数，然后计算哈希值以确保唯一性
            String argsJson = JSONUtil.toJsonStr(args);
            nameSb.append(DigestUtil.md5Hex(argsJson));
        }
        return nameSb.toString();
    }
}
