package com.vanta.starter.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 序列号生成工具类。
 * <p>
 * 该工具类基于中国时区当前时间和随机数字生成固定长度序列号，适合订单号、批次号等轻量业务标识。
 * 它不依赖数据库号段或远程发号服务，因此默认不会产生远程副作用；高并发强唯一场景应接入更严格的发号方案。
 * </p>
 */
public class SerialUtils {

    /**
     * 序列号时间部分格式化器。
     * <p>
     * 格式为年月日时分秒毫秒，示例：{@code 20260524153045123}。
     * </p>
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    /**
     * 中国标准时区。
     */
    private static final ZoneId CN_ZONE = ZoneId.of("Asia/Shanghai");

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private SerialUtils() {
    }

    /**
     * 生成指定前缀和长度的序列号。
     * <p>
     * 当目标长度小于等于前缀长度时，会截取前缀到目标长度；否则拼接时间部分，不足部分用随机数字左侧补零。
     * </p>
     *
     * @param prefix 序列号前缀。
     * @param length 序列号总长度。
     * @return 固定长度序列号。
     */
    public static String serial(String prefix, int length) {

        var time = FORMATTER.format(LocalDateTime.now(CN_ZONE));

        var remain = length - prefix.length();

        if (remain <= 0) {
            return prefix.substring(0, length);
        }

        if (time.length() >= remain) {
            return prefix + time.substring(0, remain);
        }

        var randomLen = remain - time.length();

        var random = ThreadLocalRandom.current().nextInt((int) Math.pow(10, randomLen));

        String randomStr = String.format("%0" + randomLen + "d", random);

        return prefix + time + randomStr;

    }

    /**
     * 本地手工调试入口。
     *
     * @param args 命令行参数，当前未使用。
     */
    public static void main(String[] args) {
        System.out.println(serial("P", 32));
    }
}
