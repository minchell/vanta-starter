package com.vanta.starter.security.xss.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.security.xss.enums.XssMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * XSS 过滤配置属性
 */
@ConfigurationProperties(PropertiesConstants.SECURITY_XSS)
public class XssProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 拦截路由（默认为空）
     *
     * <p>
     * 当拦截的路由配置不为空，则根据该配置执行过滤
     * </p>
     */
    private List<String> includePatterns = new ArrayList<>();

    /**
     * 放行路由（默认为空）
     */
    private List<String> excludePatterns = new ArrayList<>();

    /**
     * XSS 模式
     */
    private XssMode mode = XssMode.CLEAN;

    /**
     * 读取 Enabled 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置 Enabled 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param enabled enabled 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 读取 Include Patterns 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public List<String> getIncludePatterns() {
        return includePatterns;
    }

    /**
     * 设置 Include Patterns 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param includePatterns includePatterns 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public void setIncludePatterns(List<String> includePatterns) {
        this.includePatterns = includePatterns;
    }

    /**
     * 读取 Exclude Patterns 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    /**
     * 设置 Exclude Patterns 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param excludePatterns excludePatterns 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    /**
     * 读取 Mode 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public XssMode getMode() {
        return mode;
    }

    /**
     * 设置 Mode 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param mode mode 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public void setMode(XssMode mode) {
        this.mode = mode;
    }
}
