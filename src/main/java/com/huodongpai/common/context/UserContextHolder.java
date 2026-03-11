package com.huodongpai.common.context;

import com.huodongpai.common.model.LoginUser;
import com.huodongpai.exception.UnauthorizedException;

public final class UserContextHolder {

    private static final ThreadLocal<LoginUser> USER_CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(LoginUser loginUser) {
        USER_CONTEXT.set(loginUser);
    }

    public static LoginUser get() {
        return USER_CONTEXT.get();
    }

    public static LoginUser getRequired() {
        LoginUser loginUser = USER_CONTEXT.get();
        if (loginUser == null) {
            throw new UnauthorizedException("登录状态已失效");
        }
        return loginUser;
    }

    public static void clear() {
        USER_CONTEXT.remove();
    }
}
