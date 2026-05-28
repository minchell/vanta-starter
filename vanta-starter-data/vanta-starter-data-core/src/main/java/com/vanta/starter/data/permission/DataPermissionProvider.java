package com.vanta.starter.data.permission;

import java.util.Collections;
import java.util.Map;

/**
 * 数据权限提供器。
 * <p>
 * starter 不内置权限模型。业务系统可以通过该 SPI 返回当前请求的数据权限上下文，
 * Repository 或拦截器再根据上下文决定是否拼接权限条件。
 * </p>
 */
@FunctionalInterface
public interface DataPermissionProvider {

    /**
     * 获取当前数据权限上下文。
     *
     * @return 数据权限上下文。
     */
    Map<String, Object> currentPermissionContext();

    /**
     * 空数据权限提供器。
     *
     * @return 空上下文。
     */
    static DataPermissionProvider empty() {
        return Collections::emptyMap;
    }
}
