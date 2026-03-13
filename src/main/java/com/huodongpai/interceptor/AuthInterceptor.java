package com.huodongpai.interceptor;

import com.huodongpai.common.annotation.RequireLogin;
import com.huodongpai.common.annotation.RequireRole;
import com.huodongpai.common.context.UserContextHolder;
import com.huodongpai.common.model.LoginUser;
import com.huodongpai.exception.ForbiddenException;
import com.huodongpai.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
/**
 * 认证拦截器。
 * 在请求进入 Controller 前统一完成登录校验、角色校验和用户上下文注入。
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 权限拦截主流程：
     * 1. 判断当前处理器是否为 Controller 方法
     * 2. 查找方法或类上的权限注解
     * 3. 如果需要登录，则解析 Token 并注入 UserContextHolder
     * 4. 如果要求角色，则继续做角色匹配
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        RequireRole requireRole = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequireRole.class);
        if (requireRole == null) {
            requireRole = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequireRole.class);
        }
        boolean requireLogin = requireRole != null
                || AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequireLogin.class) != null
                || AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequireLogin.class) != null;
        if (!requireLogin) {
            return true;
        }
        LoginUser loginUser = tokenService.parseLoginUser(request);
        UserContextHolder.set(loginUser);
        if (requireRole != null && Arrays.stream(requireRole.value()).noneMatch(item -> item.getCode().equals(loginUser.getRole()))) {
            throw new ForbiddenException("无权限访问该接口");
        }
        return true;
    }

    /**
     * 请求结束时清理用户上下文，避免 ThreadLocal 污染后续请求。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }
}
