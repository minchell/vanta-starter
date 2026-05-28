package com.vanta.starter.core.exception;

/**
 * 业务异常。
 *
 * <p>用于表达可预期的业务失败，调用层可以将其转换为统一响应。</p>
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
