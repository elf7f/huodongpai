package com.huodongpai.vo.auth;

import lombok.Data;

@Data
public class LoginVO {

    private String token;
    private CurrentUserVO userInfo;
}
