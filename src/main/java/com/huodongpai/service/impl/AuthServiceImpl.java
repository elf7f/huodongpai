package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huodongpai.common.context.UserContextHolder;
import com.huodongpai.common.enums.EnableStatusEnum;
import com.huodongpai.converter.AuthConverter;
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
/**
 * 认证服务实现。
 * 负责用户名密码登录、查询当前用户信息和退出登录。
 */
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /**
     * 登录流程：
     * 1. 根据用户名查询用户
     * 2. 校验密码是否匹配
     * 3. 校验用户状态是否启用
     * 4. 生成 Token 并组装返回结果
     */
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
        return AuthConverter.toLoginVO(user, tokenService.createToken(user));
    }

    /**
     * 获取当前登录用户信息。
     * 当前用户 ID 由拦截器提前放入 UserContextHolder。
     */
    @Override
    public CurrentUserVO getCurrentUserInfo() {
        SysUser user = sysUserMapper.selectById(UserContextHolder.getRequired().getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return AuthConverter.toCurrentUserVO(user);
    }

    /**
     * 退出登录时只需要删除当前 Token 对应的 Redis 登录态即可。
     */
    @Override
    public void logout(String authorization) {
        tokenService.removeToken(authorization);
    }
}
