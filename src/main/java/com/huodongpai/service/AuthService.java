package com.huodongpai.service;

import com.huodongpai.dto.auth.LoginRequestDTO;
import com.huodongpai.vo.auth.CurrentUserVO;
import com.huodongpai.vo.auth.LoginVO;

public interface AuthService {

    LoginVO login(LoginRequestDTO requestDTO);

    CurrentUserVO getCurrentUserInfo();

    void logout(String authorization);
}
