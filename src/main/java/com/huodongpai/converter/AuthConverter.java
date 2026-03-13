package com.huodongpai.converter;

import com.huodongpai.entity.SysUser;
import com.huodongpai.vo.auth.CurrentUserVO;
import com.huodongpai.vo.auth.LoginVO;

public final class AuthConverter {

    private AuthConverter() {
    }

    /**
     * 用户实体转当前登录用户展示对象。
     */
    public static CurrentUserVO toCurrentUserVO(SysUser user) {
        CurrentUserVO currentUserVO = new CurrentUserVO();
        currentUserVO.setId(user.getId());
        currentUserVO.setUsername(user.getUsername());
        currentUserVO.setRealName(user.getRealName());
        currentUserVO.setPhone(user.getPhone());
        currentUserVO.setRole(user.getRole());
        currentUserVO.setStatus(user.getStatus());
        return currentUserVO;
    }

    /**
     * 组装登录接口返回对象。
     */
    public static LoginVO toLoginVO(SysUser user, String token) {
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(toCurrentUserVO(user));
        return loginVO;
    }
}
