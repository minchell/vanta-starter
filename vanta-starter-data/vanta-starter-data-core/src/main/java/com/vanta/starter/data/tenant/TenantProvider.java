package com.vanta.starter.data.tenant;

import java.util.Optional;

/**
 * 当前租户提供器。
 * <p>
 * starter 不内置租户模型，只通过该 SPI 允许业务系统提供租户标识。
 * </p>
 */
@FunctionalInterface
public interface TenantProvider {

    /**
     * 获取当前租户 ID。
     *
     * @return 当前租户 ID。
     */
    Optional<String> currentTenantId();
}
