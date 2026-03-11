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
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

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

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }
}
