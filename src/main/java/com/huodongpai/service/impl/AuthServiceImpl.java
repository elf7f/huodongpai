package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huodongpai.common.context.UserContextHolder;
import com.huodongpai.common.enums.EnableStatusEnum;
import com.huodongpai.dto.auth.LoginRequestDTO;
import com.huodongpai.entity.SysUser;
import com.huodongpai.exception.BusinessException;
import com.huodongpai.mapper.SysUserMapper;
import com.huodongpai.service.AuthService;
import com.huodongpai.service.TokenService;
import com.huodongpai.vo.auth.CurrentUserVO;
import com.huodongpai.vo.auth.LoginVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public LoginVO login(LoginRequestDTO requestDTO) {
        SysUser user = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, requestDTO.getUsername())
                .last("limit 1"));
        if (user == null || !passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!EnableStatusEnum.ENABLED.getCode().equals(user.getStatus())) {
            throw new BusinessException("当前账号已被禁用");
        }
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(tokenService.createToken(user));
        loginVO.setUserInfo(toCurrentUserVO(user));
        return loginVO;
    }

    @Override
    public CurrentUserVO getCurrentUserInfo() {
        SysUser user = sysUserMapper.selectById(UserContextHolder.getRequired().getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toCurrentUserVO(user);
    }

    @Override
    public void logout(String authorization) {
        tokenService.removeToken(authorization);
    }

    private CurrentUserVO toCurrentUserVO(SysUser user) {
        CurrentUserVO currentUserVO = new CurrentUserVO();
        currentUserVO.setId(user.getId());
        currentUserVO.setUsername(user.getUsername());
        currentUserVO.setRealName(user.getRealName());
        currentUserVO.setPhone(user.getPhone());
        currentUserVO.setRole(user.getRole());
        currentUserVO.setStatus(user.getStatus());
        return currentUserVO;
    }
}
