package com.huodongpai.exception;

import com.huodongpai.common.enums.ResultCodeEnum;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(ResultCodeEnum.FORBIDDEN, message);
    }
}
