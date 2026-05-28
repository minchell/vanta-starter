package com.vanta.starter.core.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;

/**
 * Spring 基础工具类。
 * <p>
 * 该类只保留不依赖 Servlet、Spring Web、Spring MVC 的通用能力，例如 Bean 获取、
 * 代理对象获取和路径模式匹配。涉及 HTTP 请求、Servlet 容器、Controller Handler
 * 或静态资源映射的能力统一放在 {@code vanta-starter-web}。
 * </p>
 */
public class SpringUtils {

    /**
     * Ant 风格路径匹配器。
     * <p>
     * 该对象来自 Spring Core，不引入 Web 运行时依赖，适合 starter-core 保持基础独立。
     * </p>
     */
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private SpringUtils() {
    }

    /**
     * 获取当前对象在 Spring 容器中的代理对象。
     *
     * @param target 目标对象，通常是当前类中的 {@code this}。
     * @param <T>    目标对象类型。
     * @return Spring 容器中与目标对象类型匹配的代理 Bean。
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(T target) {
        return (T) SpringUtil.getBean(target.getClass());
    }

    /**
     * 按类型获取 Spring Bean。
     *
     * @param clazz              Bean 类型。
     * @param ignoreNoSuchBeanEx 是否忽略 Bean 不存在异常。
     * @param <T>                Bean 类型泛型。
     * @return Bean 对象；允许忽略且未找到时返回 {@code null}。
     */
    public static <T> T getBean(Class<T> clazz, boolean ignoreNoSuchBeanEx) {
        try {
            return SpringUtil.getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            if (ignoreNoSuchBeanEx) {
                return null;
            }
            throw e;
        }
    }

    /**
     * 判断路径是否匹配任一模式。
     *
     * @param path     待匹配路径。
     * @param patterns 匹配模式列表。
     * @return 任一模式匹配成功时返回 {@code true}。
     */
    public static boolean isMatch(String path, List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> isMatch(path, pattern));
    }

    /**
     * 判断路径是否匹配任一模式。
     *
     * @param path     待匹配路径。
     * @param patterns 匹配模式数组。
     * @return 任一模式匹配成功时返回 {@code true}。
     */
    public static boolean isMatch(String path, String... patterns) {
        return Arrays.stream(patterns).anyMatch(pattern -> isMatch(path, pattern));
    }

    /**
     * 判断路径是否匹配指定模式。
     *
     * @param path    待匹配路径。
     * @param pattern 匹配模式。
     * @return 路径匹配成功时返回 {@code true}。
     */
    public static boolean isMatch(String path, String pattern) {
        return ANT_PATH_MATCHER.match(pattern, path);
    }

    /**
     * 判断路径是否匹配指定 Ant 风格模式。
     *
     * @param path    待匹配路径。
     * @param pattern Ant 风格匹配模式。
     * @return 路径匹配成功时返回 {@code true}。
     */
    public static boolean isMatchAnt(String path, String pattern) {
        return ANT_PATH_MATCHER.match(pattern, path);
    }

    /**
     * 判断路径是否匹配任一 Ant 风格模式。
     *
     * @param path     待匹配路径。
     * @param patterns Ant 风格匹配模式列表。
     * @return 任一模式匹配成功时返回 {@code true}。
     */
    public static boolean isMatchAnt(String path, List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> isMatchAnt(path, pattern));
    }
}
