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
/**
 * Token 服务实现。
 * 使用 JWT 作为身份凭证，使用 Redis 作为服务端可控登录态存储。
 */
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

    /**
     * 创建 Token。
     * 除了生成 JWT 外，还会把当前 Token 写入 Redis，便于后续登录态校验和主动失效。
     */
    @Override
    public String createToken(SysUser user) {
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Duration expireDuration = jwtTokenUtil.getExpireDuration();
        stringRedisTemplate.opsForValue().set(RedisKeyConstants.buildLoginTokenKey(token), String.valueOf(user.getId()), expireDuration);
        return token;
    }

    /**
     * 从 HttpServletRequest 中解析当前登录用户。
     * 这是拦截器校验登录态时的标准入口。
     */
    @Override
    public LoginUser parseLoginUser(HttpServletRequest request) {
        return parseLoginUserByToken(resolveToken(request), true);
    }

    /**
     * 尝试解析登录用户。
     * 常用于公开接口中“可登录可不登录”的场景，解析失败时直接返回 null。
     */
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

    /**
     * 删除当前 Token 的 Redis 登录态，用于退出登录。
     */
    @Override
    public void removeToken(String authorization) {
        String token = extractToken(authorization);
        if (StringUtils.hasText(token)) {
            stringRedisTemplate.delete(RedisKeyConstants.buildLoginTokenKey(token));
        }
    }

    /**
     * 从请求头中提取 Token。
     * 兼容标准 Authorization 头和 token 头两种写法。
     */
    @Override
    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(jwtProperties.getHeaderName());
        if (!StringUtils.hasText(authorization)) {
            authorization = request.getHeader("token");
        }
        return extractToken(authorization);
    }

    /**
     * 按 Token 解析当前登录用户。
     * 校验步骤：
     * 1. JWT 是否合法
     * 2. Redis 中登录态是否存在
     * 3. 数据库中的用户是否仍存在且为启用状态
     * 4. 可选：刷新 Redis 过期时间，形成滑动过期效果
     */
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

    /**
     * 去掉 Token 前缀，提取纯 JWT 字符串。
     */
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
