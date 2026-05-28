package com.vanta.starter.web.advice;

import com.vanta.starter.core.exception.BusinessException;
import com.vanta.starter.web.model.R;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * 全局异常处理。
 *
 * <p>业务和请求参数异常保持客户端错误状态，未知异常才按服务端错误处理。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<R<Void>> handleBusinessException(BusinessException exception) {
        log.warn("业务异常: {}", exception.getMessage());
        return fail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = resolveBindMessage(exception);
        log.warn("请求参数校验失败: {}", message);
        return fail(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<R<Void>> handleBindException(BindException exception) {
        String message = resolveBindMessage(exception);
        log.warn("请求参数绑定失败: {}", message);
        return fail(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<R<Void>> handleConstraintViolationException(ConstraintViolationException exception) {
        log.warn("请求约束校验失败: {}", exception.getMessage());
        return fail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<R<Void>> handleResponseStatusException(ResponseStatusException exception) {
        HttpStatusCode statusCode = exception.getStatusCode();
        String message = exception.getReason() == null ? statusCode.toString() : exception.getReason();
        log.warn("请求状态异常: status={}, message={}", statusCode.value(), message);
        return fail(statusCode, message);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<R<Void>> handleErrorResponse(ErrorResponse exception) {
        HttpStatusCode statusCode = exception.getStatusCode();
        String message = exception.getBody().getDetail();
        if (message == null) {
            message = statusCode.toString();
        }
        log.warn("HTTP 语义异常: status={}, message={}", statusCode.value(), message);
        return fail(statusCode, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Void>> handleException(Exception exception) {
        if (exception instanceof ErrorResponse errorResponse) {
            return handleErrorResponse(errorResponse);
        }
        log.error("系统异常", exception);
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, "系统异常，请稍后重试");
    }

    private ResponseEntity<R<Void>> fail(HttpStatusCode statusCode, String message) {
        return ResponseEntity.status(statusCode).body(R.fail(String.valueOf(statusCode.value()), message));
    }

    private String resolveBindMessage(BindException exception) {
        if (exception.getBindingResult().hasFieldErrors()) {
            return exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        if (exception.getBindingResult().hasGlobalErrors()) {
            return exception.getBindingResult().getGlobalErrors().get(0).getDefaultMessage();
        }
        return "请求参数错误";
    }
}
