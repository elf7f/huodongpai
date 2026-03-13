package com.huodongpai.common.context;

import com.huodongpai.common.model.LoginUser;
import com.huodongpai.exception.UnauthorizedException;

public final class UserContextHolder {

    /**
     * 使用 ThreadLocal 保存当前请求对应的登录用户。
     * 这样后续 Controller / Service 就不需要层层传递 userId。
     */
    private static final ThreadLocal<LoginUser> USER_CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    /**
     * 设置当前线程的登录用户。
     */
    public static void set(LoginUser loginUser) {
        USER_CONTEXT.set(loginUser);
    }

    /**
     * 获取当前线程中的登录用户，允许返回 null。
     */
    public static LoginUser get() {
        return USER_CONTEXT.get();
    }

    /**
     * 获取当前登录用户。
     * 如果当前线程中没有用户信息，说明登录态已失效或未经过拦截器处理。
     */
    public static LoginUser getRequired() {
        LoginUser loginUser = USER_CONTEXT.get();
        if (loginUser == null) {
            throw new UnauthorizedException("登录状态已失效");
        }
        return loginUser;
    }

    /**
     * 请求完成后清理 ThreadLocal，防止线程复用导致用户串号。
     */
    public static void clear() {
        USER_CONTEXT.remove();
    }
}
