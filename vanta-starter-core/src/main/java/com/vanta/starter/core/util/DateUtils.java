package com.vanta.starter.core.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 中国时区日期时间工具类。
 * <p>
 * 该工具类统一使用 {@code Asia/Shanghai} 时区进行时间获取和时间戳转换，避免不同运行环境默认时区不一致导致业务时间漂移。
 * 方法只做本地时间换算，不访问系统外部资源，也不持有可变状态。
 * </p>
 */
public class DateUtils {

    /**
     * 中国标准时区。
     * <p>
     * 使用 IANA 标准时区标识 {@code Asia/Shanghai}，用于所有本地时间和时间戳互转。
     * </p>
     */
    public static final ZoneId CN_ZONE = ZoneId.of("Asia/Shanghai");

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private DateUtils() {
    }

    /**
     * 获取中国时区下的当前本地日期时间。
     *
     * @return 中国时区当前 {@link LocalDateTime}。
     */
    public static LocalDateTime cnNow() {

        return LocalDateTime.now(CN_ZONE);
    }

    /**
     * 把中国时区本地日期时间转换为毫秒时间戳。
     *
     * @param dateTime 中国时区语义下的本地日期时间；为 {@code null} 时直接返回 {@code null}。
     * @return 自 Unix Epoch 起的毫秒时间戳；入参为 {@code null} 时返回 {@code null}。
     */
    public static Long convertToCNTimestamp(LocalDateTime dateTime) {

        if (null == dateTime) return null;

        return dateTime.atZone(CN_ZONE).toInstant().toEpochMilli();
    }

    /**
     * 把毫秒时间戳转换为中国时区本地日期时间。
     *
     * @param timestamp 自 Unix Epoch 起的毫秒时间戳；为 {@code null} 时直接返回 {@code null}。
     * @return 中国时区本地日期时间；入参为 {@code null} 时返回 {@code null}。
     */
    public static LocalDateTime convertToCNDateTime(Long timestamp) {

        if (null == timestamp) return null;

        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                CN_ZONE
        );
    }

    /**
     * 把瞬时时间点转换为中国时区本地日期时间。
     *
     * @param instant 瞬时时间点；为 {@code null} 时直接返回 {@code null}。
     * @return 中国时区本地日期时间；入参为 {@code null} 时返回 {@code null}。
     */
    public static LocalDateTime convertToCNDateTime(Instant instant) {

        if (null == instant) return null;

        return LocalDateTime.ofInstant(instant, CN_ZONE);
    }

}
