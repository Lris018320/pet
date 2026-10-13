package com.pet.platform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成/解析 token，承载用户身份与角色信息
 */
@Component
public class JwtUtil {

    @Value("${pet.jwt.secret}")
    private String secret;

    @Value("${pet.jwt.expire}")
    private Long expireSeconds;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 token
     *
     * @param userId   用户ID
     * @param account  账号
     * @param role     角色：user / admin
     */
    public String generate(Long userId, String account, String role) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("userId", userId);
        claims.put("account", account);
        claims.put("role", role);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireSeconds * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 token，失败返回 null
     */
    public Claims parse(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Long getUserId(Claims claims) {
        Object v = claims.get("userId");
        if (v == null) {
            return null;
        }
        return ((Number) v).longValue();
    }

    public String getAccount(Claims claims) {
        Object v = claims.get("account");
        return v == null ? null : v.toString();
    }

    public String getRole(Claims claims) {
        Object v = claims.get("role");
        return v == null ? null : v.toString();
    }
}
