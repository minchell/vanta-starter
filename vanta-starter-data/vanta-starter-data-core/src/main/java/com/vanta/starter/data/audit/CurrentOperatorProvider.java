package com.vanta.starter.data.audit;

import java.util.Optional;

/**
 * 当前操作者提供器。
 * <p>
 * starter 只提供 SPI，不绑定任何登录体系。业务系统可以接入 Sa-Token、Spring Security
 * 或自定义上下文后返回当前操作者 ID。
 * </p>
 */
@FunctionalInterface
public interface CurrentOperatorProvider {

    /**
     * 获取当前操作者 ID。
     *
     * @return 当前操作者 ID。
     */
    Optional<Long> currentOperatorId();
}
