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
/**
 * 全局异常处理器。
 * 统一把各种异常转换成前端可识别的 ApiResponse 结构。
 */
public class GlobalExceptionHandler {

    /**
     * 业务异常处理。
     * 根据异常中的 code 决定最终返回 401、403 或 400。
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        ResultCodeEnum resultCodeEnum = Objects.equals(exception.getCode(), ResultCodeEnum.UNAUTHORIZED.getCode())
                ? ResultCodeEnum.UNAUTHORIZED
                : Objects.equals(exception.getCode(), ResultCodeEnum.FORBIDDEN.getCode())
                ? ResultCodeEnum.FORBIDDEN
                : ResultCodeEnum.BAD_REQUEST;
        return ApiResponse.fail(resultCodeEnum, exception.getMessage());
    }

    /**
     * 参数校验异常处理。
     * 优先返回第一个具体字段错误信息，便于前端直接提示用户。
     */
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

    /**
     * 请求体格式错误，例如 JSON 结构不合法。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ApiResponse.fail(ResultCodeEnum.BAD_REQUEST, "请求体格式错误");
    }

    /**
     * 唯一索引冲突异常处理。
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<Void> handleDuplicateKeyException(DuplicateKeyException exception) {
        return ApiResponse.fail(ResultCodeEnum.BAD_REQUEST, "数据已存在，请勿重复提交");
    }

    /**
     * 兜底异常处理。
     * 避免未捕获异常直接把堆栈暴露给前端。
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        return ApiResponse.fail(ResultCodeEnum.SYSTEM_ERROR, exception.getMessage() == null ? "系统异常" : exception.getMessage());
    }
}
