package com.vanta.starter.web.advice;

import com.vanta.starter.core.exception.BusinessException;
import com.vanta.starter.web.model.R;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionAdviceTest {
    private final GlobalExceptionAdvice advice = new GlobalExceptionAdvice();

    @Test
    void businessExceptionShouldReturnBadRequest() {
        ResponseEntity<R<Void>> response = advice.handleBusinessException(new BusinessException("业务失败"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().getCode());
        assertEquals("业务失败", response.getBody().getMsg());
    }

    @Test
    void constraintViolationShouldReturnBadRequest() {
        ResponseEntity<R<Void>> response =
                advice.handleConstraintViolationException(new ConstraintViolationException("参数错误", null));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().getCode());
    }

    @Test
    void bindExceptionShouldReturnBadRequest() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new QueryParam(), "queryParam");
        bindingResult.addError(new FieldError("queryParam", "name", "名称不能为空"));

        ResponseEntity<R<Void>> response = advice.handleBindException(new org.springframework.validation.BindException(bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().getCode());
        assertEquals("名称不能为空", response.getBody().getMsg());
    }

    @Test
    void methodArgumentNotValidShouldReturnBadRequest() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new QueryParam(), "queryParam");
        bindingResult.addError(new FieldError("queryParam", "name", "名称不能为空"));
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionAdviceTest.class.getDeclaredMethod("accept", QueryParam.class),
                0
        );
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<R<Void>> response = advice.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().getCode());
        assertEquals("名称不能为空", response.getBody().getMsg());
    }

    @Test
    void responseStatusExceptionShouldPreserveStatus() {
        ResponseEntity<R<Void>> response =
                advice.handleResponseStatusException(new ResponseStatusException(HttpStatus.NOT_FOUND, "不存在"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404", response.getBody().getCode());
        assertEquals("不存在", response.getBody().getMsg());
    }

    @Test
    void springErrorResponseShouldPreserveStatus() {
        ErrorResponseException exception = new ErrorResponseException(HttpStatus.METHOD_NOT_ALLOWED);

        ResponseEntity<R<Void>> response = advice.handleErrorResponse(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("405", response.getBody().getCode());
    }

    @Test
    void unexpectedExceptionShouldReturnInternalServerError() {
        ResponseEntity<R<Void>> response = advice.handleException(new IllegalStateException("失败"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().getCode());
    }

    @SuppressWarnings("unused")
    private void accept(QueryParam queryParam) {
    }

    private static class QueryParam {
        private String name;
    }
}
