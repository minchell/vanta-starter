package com.vanta.starter.scheduler.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Vanta 调度能力配置。
 *
 * <p>首版只提供本地任务执行抽象，不会注册定时任务，也不会连接远程调度中心。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.SCHEDULER)
public class SchedulerProperties {

    /**
     * 是否启用 Vanta 调度执行器。
     * -- GETTER --
     * 读取调度能力启用状态。
     * <p>
     * <p>
     * -- SETTER --
     * 设置调度能力启用状态。
     *
     * @return true 表示注册调度执行器，false 表示不启用调度 starter
     * @param enabled true 表示启用调度执行器，false 表示关闭自动配置
     */
    private boolean enabled = false;

    /**
     * 调度执行器类型。首版支持 local。
     * -- GETTER --
     * 读取调度执行器类型。
     * <p>
     * <p>
     * -- SETTER --
     * 设置调度执行器类型。
     *
     * @return 当前调度执行器类型，默认 local
     * @param type 调度执行器类型；首版仅支持 local，后续可扩展 xxl-job、quartz 等类型
     */
    private String type = "local";

    /**
     * 获取是否启用 Vanta 调度执行器。
     *
     * @return 是否启用 Vanta 调度执行器
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 Vanta 调度执行器。
     *
     * @param enabled 是否启用 Vanta 调度执行器
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取调度执行器类型。首版支持 local。
     *
     * @return 调度执行器类型。首版支持 local
     */
    public String getType() {
        return type;
    }

    /**
     * 设置调度执行器类型。首版支持 local。
     *
     * @param type 调度执行器类型。首版支持 local
     */
    public void setType(String type) {
        this.type = type;
    }
}
