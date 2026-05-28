package com.vanta.starter.apidoc.util;

import org.springdoc.core.models.GroupedOpenApi;


/**
 * ApiDocUtils 类。
 * <p>该类型属于 接口文档能力，负责提供当前能力内部可复用的辅助操作。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class ApiDocUtils {

    private ApiDocUtils() {
    }

    /**
     * 构建分组接口文档
     *
     * @param group        分组名称
     * @param pathsToMatch 路径匹配
     * @return GroupedOpenApi
     * @since 2.13.0
     */
    public static GroupedOpenApi buildGroupedOpenApi(String group, String... pathsToMatch) {
        return buildGroupedOpenApi(group, group, pathsToMatch);
    }

    /**
     * 构建分组接口文档
     *
     * @param group        分组名称
     * @param displayName  分组显示名称
     * @param pathsToMatch 路径匹配
     * @return GroupedOpenApi
     * @since 2.13.0
     */
    public static GroupedOpenApi buildGroupedOpenApi(String group, String displayName, String... pathsToMatch) {
        return GroupedOpenApi.builder().group(group).displayName(displayName).pathsToMatch(pathsToMatch).build();
    }
}