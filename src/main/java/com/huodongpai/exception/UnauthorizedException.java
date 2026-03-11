package com.huodongpai.exception;

import com.huodongpai.common.enums.ResultCodeEnum;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(ResultCodeEnum.UNAUTHORIZED, message);
    }
}
