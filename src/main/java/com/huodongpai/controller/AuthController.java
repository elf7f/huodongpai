package com.huodongpai.controller;

import com.huodongpai.common.annotation.RequireLogin;
import com.huodongpai.common.result.ApiResponse;
import com.huodongpai.dto.auth.LoginRequestDTO;
import com.huodongpai.service.AuthService;
import com.huodongpai.vo.auth.CurrentUserVO;
import com.huodongpai.vo.auth.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        return ApiResponse.success(authService.login(requestDTO));
    }

    @RequireLogin
    @GetMapping("/info")
    public ApiResponse<CurrentUserVO> info() {
        return ApiResponse.success(authService.getCurrentUserInfo());
    }

    @RequireLogin
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        authService.logout(request.getHeader("Authorization"));
        return ApiResponse.success();
    }
}
