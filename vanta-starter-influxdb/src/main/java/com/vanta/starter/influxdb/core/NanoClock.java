package com.vanta.starter.influxdb.core;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * InfluxDB 纳秒时间工具。
 *
 * <p>InfluxDB 写入高频数据时，毫秒时间戳容易重复。该工具基于系统毫秒时间
 * 和 {@link System#nanoTime()} 生成纳秒时间，并保证同一 JVM 内单调递增。</p>
 */
public final class NanoClock {

    /**
     * 工具类初始化时的系统毫秒时间。
     * <p>作为纳秒时间戳的墙钟时间基准。</p>
     */
    private static final long INIT_MILLIS = System.currentTimeMillis();

    /**
     * 工具类初始化时的 JVM 单调纳秒时间。
     * <p>用于计算从初始化到当前调用之间经过的纳秒数。</p>
     */
    private static final long INIT_NANOS = System.nanoTime();

    /**
     * 当前 JVM 已返回过的最大纳秒时间戳。
     * <p>通过 CAS 更新保证并发场景下返回值单调递增。</p>
     */
    private static final AtomicLong LAST_NANO = new AtomicLong(0);

    private NanoClock() {
    }

    /**
     * 生成当前 JVM 内单调递增的纳秒时间戳。
     *
     * @return 当前纳秒时间戳
     */
    public static long now() {
        long elapsedNanos = System.nanoTime() - INIT_NANOS;
        long candidate = INIT_MILLIS * 1_000_000L + elapsedNanos;
        return LAST_NANO.updateAndGet(last -> Math.max(candidate, last + 1));
    }

    /**
     * 生成当前纳秒时间戳对应的 Instant。
     *
     * @return 当前 Instant
     */
    public static Instant instant() {
        return instant(now());
    }

    /**
     * 将纳秒时间戳转换为 Instant。
     *
     * @param nanoTimestamp 自 Epoch 起算的纳秒时间戳
     * @return 对应的 Instant
     */
    public static Instant instant(long nanoTimestamp) {
        long epochSeconds = nanoTimestamp / 1_000_000_000L;
        int nanos = (int) (nanoTimestamp % 1_000_000_000L);
        return Instant.ofEpochSecond(epochSeconds, nanos);
    }
}
