package com.vanta.starter.cache.redis.handler;

import cn.hutool.core.text.CharSequenceUtil;
import org.redisson.api.NameMapper;


/**
 * NameMapperHandler 类。
 * <p>该类型属于 缓存能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class NameMapperHandler implements NameMapper {

    /**
     * keyPrefix 字段。
     * <p>用于保存 缓存能力 的资源命名配置，用于定位消息、索引、节点或业务对象。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final String keyPrefix;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param keyPrefix keyPrefix 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public NameMapperHandler(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    /**
     * 转换 map 的输入数据。
     * 该方法负责在底层客户端模型和业务可读模型之间做边界转换，避免调用方直接依赖底层细节。
     *
     * @param name name 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String map(String name) {
        if (CharSequenceUtil.isNotBlank(name) && !name.startsWith(keyPrefix)) {
            return keyPrefix + name;
        }
        return name;
    }

    /**
     * 执行 unmap 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param name name 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String unmap(String name) {
        if (CharSequenceUtil.isNotBlank(name) && name.startsWith(keyPrefix)) {
            return name.substring(keyPrefix.length());
        }
        return name;
    }
}
