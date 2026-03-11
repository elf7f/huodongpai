package com.huodongpai.common.result;

import com.huodongpai.common.enums.ResultCodeEnum;
import lombok.Data;

@Data
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success() {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ResultCodeEnum.SUCCESS.getCode());
        response.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return response;
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = success();
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> fail(ResultCodeEnum resultCodeEnum, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(resultCodeEnum.getCode());
        response.setMessage(message);
        return response;
    }
}
