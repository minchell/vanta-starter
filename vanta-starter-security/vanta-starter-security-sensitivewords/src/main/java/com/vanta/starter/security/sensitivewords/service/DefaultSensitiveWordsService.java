package com.vanta.starter.security.sensitivewords.service;

import cn.hutool.dfa.WordTree;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.List;

/**
 * 默认敏感词服务
 */
@ConditionalOnBean(SensitiveWordsConfig.class)
@ConditionalOnMissingBean(SensitiveWordsService.class)
public class DefaultSensitiveWordsService implements SensitiveWordsService {

    /**
     * sensitiveWordsConfig 字段。
     * <p>用于保存 安全防护能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final SensitiveWordsConfig sensitiveWordsConfig;
    /**
     * tree 字段。
     * <p>用于保存 安全防护能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final WordTree tree = new WordTree();

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param sensitiveWordsConfig sensitiveWordsConfig 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public DefaultSensitiveWordsService(SensitiveWordsConfig sensitiveWordsConfig) {
        this.sensitiveWordsConfig = sensitiveWordsConfig;
        if (sensitiveWordsConfig != null && sensitiveWordsConfig.getWords() != null) {
            tree.addWords(sensitiveWordsConfig.getWords());
        }
    }

    /**
     * 执行 check 逻辑。
     * 该方法属于 安全防护能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param content content 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public List<String> check(String content) {
        return tree.matchAll(content, -1, false, true);
    }
}
