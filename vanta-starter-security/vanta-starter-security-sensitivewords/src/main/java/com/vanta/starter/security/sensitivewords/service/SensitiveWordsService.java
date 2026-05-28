package com.vanta.starter.security.sensitivewords.service;

import java.util.List;

/**
 * 敏感词服务接口
 */
public interface SensitiveWordsService {

    /**
     * 检查敏感词
     *
     * @param content 待检测字符串
     * @return 敏感词列表
     */
    List<String> check(String content);
}
