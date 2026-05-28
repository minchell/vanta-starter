package com.vanta.starter.security.sensitivewords.service;

import com.vanta.starter.security.sensitivewords.autoconfigure.SensitiveWordsProperties;

import java.util.List;

/**
 * 默认敏感词配置
 */
public class DefaultSensitiveWordsConfig implements SensitiveWordsConfig {

    /**
     * properties 字段。
     * <p>用于保存 安全防护能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final SensitiveWordsProperties properties;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param properties properties 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public DefaultSensitiveWordsConfig(SensitiveWordsProperties properties) {
        this.properties = properties;
    }

    /**
     * 读取 Words 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public List<String> getWords() {
        if (properties.getValues() != null) {
            return properties.getValues();
        }
        return List.of();
    }
}
