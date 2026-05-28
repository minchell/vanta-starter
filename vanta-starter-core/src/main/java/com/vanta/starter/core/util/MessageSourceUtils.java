package com.vanta.starter.core.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 国际化消息读取工具类。
 * <p>
 * 该工具类从 Spring 容器获取 {@link MessageSource}，并按当前线程的 {@link LocaleContextHolder} 语言环境解析消息。
 * 如果消息编码不存在或解析失败，会返回调用方指定的默认消息，避免国际化缺失导致主流程异常。
 * </p>
 */
public class MessageSourceUtils {

    /**
     * Spring 国际化消息源。
     */
    private static final MessageSource MESSAGE_SOURCE = SpringUtil.getBean(MessageSource.class);

    /**
     * 无参数消息模板使用的空参数数组。
     */
    private static final Object[] EMPTY_ARGS = {};

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private MessageSourceUtils() {
    }

    /**
     * 根据消息编码获取国际化消息。
     *
     * @param code 消息编码。
     * @return 国际化后的消息；解析失败时返回消息编码本身。
     */
    public static String getMessage(String code) {
        return getMessage(code, EMPTY_ARGS);
    }

    /**
     * 根据消息编码和参数获取国际化消息。
     *
     * @param code 消息编码。
     * @param args 消息模板参数。
     * @return 国际化后的消息；解析失败时返回消息编码本身。
     */
    public static String getMessage(String code, Object... args) {
        return getMessage(code, code, args);
    }

    /**
     * 根据消息编码和默认消息获取国际化消息。
     *
     * @param code           消息编码。
     * @param defaultMessage 默认消息。
     * @return 国际化后的消息；解析失败时返回默认消息。
     */
    public static String getMessage(String code, String defaultMessage) {
        return getMessage(code, defaultMessage, EMPTY_ARGS);
    }

    /**
     * 根据消息编码、默认消息和参数获取国际化消息。
     *
     * @param code           消息编码。
     * @param defaultMessage 默认消息。
     * @param args           消息模板参数。
     * @return 国际化后的消息；解析失败时返回默认消息。
     */
    public static String getMessage(String code, String defaultMessage, Object... args) {
        try {
            return MESSAGE_SOURCE.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return defaultMessage;
        }
    }
}
