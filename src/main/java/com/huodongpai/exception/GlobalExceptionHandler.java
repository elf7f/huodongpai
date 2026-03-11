package com.huodongpai.exception;

import com.huodongpai.common.enums.ResultCodeEnum;
import com.huodongpai.common.result.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        ResultCodeEnum resultCodeEnum = Objects.equals(exception.getCode(), ResultCodeEnum.UNAUTHORIZED.getCode())
                ? ResultCodeEnum.UNAUTHORIZED
                : Objects.equals(exception.getCode(), ResultCodeEnum.FORBIDDEN.getCode())
                ? ResultCodeEnum.FORBIDDEN
                : ResultCodeEnum.BAD_REQUEST;
        return ApiResponse.fail(resultCodeEnum, exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public ApiResponse<Void> handleValidationException(Exception exception) {
        String message = "请求参数错误";
        if (exception instanceof MethodArgumentNotValidException ex) {
            message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        } else if (exception instanceof BindException ex) {
            message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        } else if (exception instanceof ConstraintViolationException ex) {
            message = ex.getConstraintViolations().iterator().next().getMessage();
        }
        return ApiResponse.fail(ResultCodeEnum.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ApiResponse.fail(ResultCodeEnum.BAD_REQUEST, "请求体格式错误");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<Void> handleDuplicateKeyException(DuplicateKeyException exception) {
        return ApiResponse.fail(ResultCodeEnum.BAD_REQUEST, "数据已存在，请勿重复提交");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        return ApiResponse.fail(ResultCodeEnum.SYSTEM_ERROR, exception.getMessage() == null ? "系统异常" : exception.getMessage());
    }
}
