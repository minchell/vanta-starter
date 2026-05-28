package com.vanta.starter.core.util;

import com.vanta.starter.core.constant.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 异常处理辅助工具类。
 * <p>
 * 该工具类封装“异常转默认值”“异常转自定义异常”“异步线程异常打印”等常见模板代码。
 * 调用方应只在明确接受降级语义时使用默认值方法，避免把真正需要暴露的问题静默吞掉。
 * </p>
 */
public class ExceptionUtils {

    /**
     * 当前工具类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private ExceptionUtils() {
    }

    /**
     * 打印线程异常信息
     *
     * @param runnable  线程执行内容
     * @param throwable 异常
     */
    public static void printException(Runnable runnable, Throwable throwable) {
        if (throwable == null && runnable instanceof Future<?> future) {
            try {
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException e) {
                throwable = e;
            } catch (ExecutionException e) {
                throwable = e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (throwable != null) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    /**
     * 如果有异常，返回 null
     *
     * @param exSupplier 可能会出现异常的方法执行
     * @param <T>        执行结果类型。
     * @return 方法执行结果；出现异常时返回 {@code null}。
     */
    public static <T> T exToNull(ExSupplier<T> exSupplier) {
        return exToDefault(exSupplier, null);
    }

    /**
     * 如果有异常，执行异常处理
     *
     * @param supplier   可能会出现异常的方法执行
     * @param exConsumer 异常处理
     * @param <T>        执行结果类型。
     * @return 方法执行结果；出现异常时返回 {@code null}。
     */
    public static <T> T exToNull(ExSupplier<T> supplier, Consumer<Exception> exConsumer) {
        return exToDefault(supplier, null, exConsumer);
    }

    /**
     * 如果有异常，返回空字符串
     *
     * @param exSupplier 可能会出现异常的方法执行
     * @return 方法执行结果；出现异常时返回空字符串。
     */
    public static String exToBlank(ExSupplier<String> exSupplier) {
        return exToDefault(exSupplier, StringConstants.EMPTY);
    }

    /**
     * 如果有异常，返回默认值
     *
     * @param exSupplier   可能会出现异常的方法执行
     * @param defaultValue 默认值
     * @param <T>          执行结果类型。
     * @return 方法执行结果；出现异常时返回指定默认值。
     */
    public static <T> T exToDefault(ExSupplier<T> exSupplier, T defaultValue) {
        return exToDefault(exSupplier, defaultValue, null);
    }

    /**
     * 如果有异常，抛出自定义异常
     *
     * @param exSupplier      可能会出现异常的方法执行
     * @param exceptionMapper 异常转换函数
     * @param <T>             返回值类型
     * @param <E>             自定义异常类型
     * @return 执行结果
     * @throws E 自定义异常
     */
    public static <T, E extends Exception> T exToThrow(ExSupplier<T> exSupplier,
                                                       Function<Exception, E> exceptionMapper) throws E {
        try {
            return exSupplier.get();
        } catch (Exception e) {
            throw exceptionMapper.apply(e);
        }
    }

    /**
     * 如果有异常，执行异常处理，返回默认值
     *
     * @param exSupplier   可能会出现异常的方法执行
     * @param defaultValue 默认值
     * @param exConsumer   异常处理
     * @param <T>          执行结果类型。
     * @return 方法执行结果；出现异常时返回指定默认值。
     */
    public static <T> T exToDefault(ExSupplier<T> exSupplier, T defaultValue, Consumer<Exception> exConsumer) {
        try {
            return exSupplier.get();
        } catch (Exception e) {
            if (exConsumer != null) {
                exConsumer.accept(e);
            }
            return defaultValue;
        }
    }

    /**
     * 允许抛出受检异常的值提供者。
     * <p>
     * Java 标准 {@link java.util.function.Supplier} 不能声明受检异常，该接口用于包装可能抛出异常的读取、转换或远程调用逻辑。
     * </p>
     *
     * @param <T> 返回值类型。
     */
    public interface ExSupplier<T> {
        /**
         * 获取返回值。
         *
         * @return 执行结果。
         * @throws Exception 执行过程中出现任意异常时抛出。
         */
        T get() throws Exception;

    }
}
