package com.huodongpai.common.util;

import com.huodongpai.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
/**
 * JWT 工具类。
 * 负责生成、解析 Token 以及统一管理 Token 过期时间。
 */
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 生成 JWT。
     * Token 内只放最小身份信息：userId、username、role。
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + getExpireDuration().toMillis());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析 JWT 并返回 Claims。
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取 Token 过期时长。
     */
    public Duration getExpireDuration() {
        return Duration.ofHours(jwtProperties.getExpireHours());
    }

    /**
     * 生成签名密钥。
     */
    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
