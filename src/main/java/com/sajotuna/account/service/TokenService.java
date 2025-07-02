package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Environment env;
    private final byte[] secretKey;
    private final static Long ACCESS_TOKEN_EXPIRES = 1800 * 1000L;
    private final static Long REFRESH_TOKEN_EXPIRES = 24 * 60 * 60 * 1000L;

    public TokenService(Environment env, RedisTemplate<String, Object> redisTemplate) {
        this.env = env;
        this.redisTemplate = redisTemplate;
        this.secretKey = env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8);
    }

    private String getToken(Claims claims, User user, Long tokenExpires) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenExpires))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    public String getAccessTokenFromRefreshToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(refreshToken).getBody();
        Long userId = Long.valueOf(getIdFromToken(refreshToken));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRES))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    public String getAccessToken(Claims claims, User user) {
        return getToken(claims, user, ACCESS_TOKEN_EXPIRES);
    }

    public String getRefreshToken(Claims claims, User user) {
        return getToken(claims, user, REFRESH_TOKEN_EXPIRES);
    }

    public void saveRefreshToken(Long id, String refreshToken){
        String key = "refresh_token:" + id;
        redisTemplate.opsForValue().set(key, refreshToken, 1, TimeUnit.DAYS);
    }

    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }


    public String getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
