package com.vanta.starter.log.model;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.util.SpringUtils;
import com.vanta.starter.log.enums.Include;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 日志 starter 全局配置。
 *
 * <p>配置前缀来自 {@link PropertiesConstants#LOG}，用于控制日志能力是否启用、采集哪些请求/响应字段、
 * 哪些路径不进入日志链路，以及访问日志打印的细节。</p>
 */
@ConfigurationProperties(PropertiesConstants.LOG)
public class LogProperties {

    /**
     * 是否启用日志能力。
     *
     * <p>关闭后，AOP 或拦截器日志自动配置不会生效。</p>
     */
    private boolean enabled = false;

    /**
     * 访问日志打印配置。
     *
     * <p>该配置只控制控制台访问日志打印，不等同于接口操作日志持久化。</p>
     */
    @NestedConfigurationProperty
    private AccessLogProperties accessLog = new AccessLogProperties();

    /**
     * 默认采集字段集合。
     *
     * <p>接口或类上的 {@code @Log} 可以在此基础上追加 includes 或排除 excludes。</p>
     */
    private Set<Include> includes = Include.defaultIncludes();

    /**
     * 不进入日志记录链路的路径模式。
     *
     * <p>支持 Spring Ant 风格路径匹配，适合排除健康检查、静态资源、文档资源等高频低价值请求。</p>
     */
    private List<String> excludePatterns = new ArrayList<>();

    /**
     * 读取日志能力启用状态。
     *
     * @return true 表示启用日志能力，false 表示关闭日志能力
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置日志能力启用状态。
     *
     * @param enabled true 表示启用日志能力，false 表示关闭日志能力
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 读取默认采集字段集合。
     *
     * @return 默认采集字段集合
     */
    public Set<Include> getIncludes() {
        return includes;
    }

    /**
     * 设置默认采集字段集合。
     *
     * @param includes 默认采集字段集合
     */
    public void setIncludes(Set<Include> includes) {
        this.includes = includes;
    }

    /**
     * 读取日志排除路径模式。
     *
     * @return 不进入日志链路的路径模式列表
     */
    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    /**
     * 设置日志排除路径模式。
     *
     * @param excludePatterns 不进入日志链路的路径模式列表
     */
    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    /**
     * 读取访问日志打印配置。
     *
     * @return 访问日志打印配置
     */
    public AccessLogProperties getAccessLog() {
        return accessLog;
    }

    /**
     * 设置访问日志打印配置。
     *
     * @param accessLog 访问日志打印配置
     */
    public void setAccessLog(AccessLogProperties accessLog) {
        this.accessLog = accessLog;
    }

    /**
     * 判断指定 URI 是否命中日志排除路径。
     *
     * @param uri 请求 URI
     * @return true 表示该 URI 应跳过日志记录，false 表示需要进入日志链路
     */
    public boolean isMatchExcludeUri(String uri) {
        return this.getExcludePatterns().stream().anyMatch(pattern -> SpringUtils.isMatch(uri, pattern));
    }
}
