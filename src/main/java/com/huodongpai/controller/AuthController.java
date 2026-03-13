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
/**
 * 认证相关接口。
 * 负责登录、获取当前登录用户信息以及退出登录。
 */
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录。
     * 校验用户名密码后返回 JWT Token 和当前用户信息。
     */
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        return ApiResponse.success(authService.login(requestDTO));
    }

    /**
     * 获取当前登录用户信息。
     * 该接口依赖拦截器提前完成登录校验和用户上下文注入。
     */
    @RequireLogin
    @GetMapping("/info")
    public ApiResponse<CurrentUserVO> info() {
        return ApiResponse.success(authService.getCurrentUserInfo());
    }

    /**
     * 退出登录。
     * 通过请求头中的 Token 删除 Redis 登录态，使当前会话立即失效。
     */
    @RequireLogin
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        authService.logout(request.getHeader("Authorization"));
        return ApiResponse.success();
    }
}
