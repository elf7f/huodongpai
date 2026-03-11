package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huodongpai.common.constant.RedisKeyConstants;
import com.huodongpai.common.enums.EnableStatusEnum;
import com.huodongpai.common.model.LoginUser;
import com.huodongpai.common.util.JwtTokenUtil;
import com.huodongpai.config.JwtProperties;
import com.huodongpai.entity.SysUser;
import com.huodongpai.exception.UnauthorizedException;
import com.huodongpai.mapper.SysUserMapper;
import com.huodongpai.service.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TokenServiceImpl implements TokenService {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final SysUserMapper sysUserMapper;

    public TokenServiceImpl(JwtTokenUtil jwtTokenUtil,
                            JwtProperties jwtProperties,
                            StringRedisTemplate stringRedisTemplate,
                            SysUserMapper sysUserMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtProperties = jwtProperties;
        this.stringRedisTemplate = stringRedisTemplate;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public String createToken(SysUser user) {
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Duration expireDuration = jwtTokenUtil.getExpireDuration();
        stringRedisTemplate.opsForValue().set(RedisKeyConstants.buildLoginTokenKey(token), String.valueOf(user.getId()), expireDuration);
        return token;
    }

    @Override
    public LoginUser parseLoginUser(HttpServletRequest request) {
        return parseLoginUserByToken(resolveToken(request), true);
    }

    @Override
    public LoginUser tryParseLoginUser(String authorization) {
        String token = extractToken(authorization);
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return parseLoginUserByToken(token, false);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public void removeToken(String authorization) {
        String token = extractToken(authorization);
        if (StringUtils.hasText(token)) {
            stringRedisTemplate.delete(RedisKeyConstants.buildLoginTokenKey(token));
        }
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(jwtProperties.getHeaderName());
        if (!StringUtils.hasText(authorization)) {
            authorization = request.getHeader("token");
        }
        return extractToken(authorization);
    }

    private LoginUser parseLoginUserByToken(String token, boolean refreshExpire) {
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("请先登录");
        }
        Claims claims;
        try {
            claims = jwtTokenUtil.parseToken(token);
        } catch (Exception exception) {
            throw new UnauthorizedException("登录状态已过期，请重新登录");
        }
        Long userId = Long.parseLong(claims.getSubject());
        String cacheKey = RedisKeyConstants.buildLoginTokenKey(token);
        String cachedUserId = stringRedisTemplate.opsForValue().get(cacheKey);
        if (!StringUtils.hasText(cachedUserId) || !cachedUserId.equals(String.valueOf(userId))) {
            throw new UnauthorizedException("登录状态已过期，请重新登录");
        }
        SysUser user = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getId, userId)
                .last("limit 1"));
        if (user == null || !EnableStatusEnum.ENABLED.getCode().equals(user.getStatus())) {
            throw new UnauthorizedException("用户不存在或已被禁用");
        }
        if (refreshExpire) {
            stringRedisTemplate.expire(cacheKey, jwtTokenUtil.getExpireDuration());
        }
        return LoginUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        if (authorization.startsWith(jwtProperties.getTokenPrefix())) {
            return authorization.substring(jwtProperties.getTokenPrefix().length()).trim();
        }
        return authorization.trim();
    }
}
