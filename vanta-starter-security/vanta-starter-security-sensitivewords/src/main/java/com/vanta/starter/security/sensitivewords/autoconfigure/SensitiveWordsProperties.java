package com.vanta.starter.security.sensitivewords.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 敏感词配置属性
 */
@ConfigurationProperties(PropertiesConstants.SECURITY_SENSITIVE_WORDS)
public class SensitiveWordsProperties {

    private boolean enabled = false;

    /**
     * 敏感词列表
     */
    private List<String> values;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 读取 Values 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * 设置 Values 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param values values 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public void setValues(List<String> values) {
        this.values = values;
    }
}
