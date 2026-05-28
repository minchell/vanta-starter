package com.vanta.starter.security.sensitivewords.service;

import java.util.List;

/**
 * 敏感词配置接口
 */
public interface SensitiveWordsConfig {

    /**
     * 获取敏感词列表
     *
     * @return 敏感词列表
     */
    List<String> getWords();
}
