package com.huodongpai.service;

import com.huodongpai.common.model.LoginUser;
import com.huodongpai.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;

public interface TokenService {

    String createToken(SysUser user);

    LoginUser parseLoginUser(HttpServletRequest request);

    LoginUser tryParseLoginUser(String authorization);

    void removeToken(String authorization);

    String resolveToken(HttpServletRequest request);
}
