package com.sajotuna.account.service;

import com.sajotuna.account.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    Environment env;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    TokenService tokenService;

    String secret = "my-super-secret-key-my-super-secret-key";

    @BeforeEach
    void setUp() {
        when(env.getProperty("token.secret")).thenReturn(secret);
        tokenService = new TokenService(env, redisTemplate);
    }

    @Test
    void validateAccessToken() {
        Claims claims = Jwts.claims().setSubject("0");
        User user = new User();

        String token = tokenService.getAccessToken(claims, user);

        assertTrue(tokenService.validate(token));
        assertEquals("0", tokenService.getIdFromToken(token));
    }

    @Test
    void testInvalidTokenValidation() {
        String fakeToken = "malformed.token.example";
        assertFalse(tokenService.validate(fakeToken));
    }

    @Test
    void testSaveRefreshToken() {
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        tokenService.saveRefreshToken(0L, "refresh123");

        verify(valueOps).set("refresh_token:0", "refresh123", 1, TimeUnit.DAYS);
    }

    @Test
    void testValidateRefreshToken_valid() {
        String refreshToken = tokenService.getRefreshToken(Jwts.claims().setSubject("0"), new User());
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("refresh_token:0")).thenReturn(refreshToken);

        assertTrue(tokenService.validateRefreshToken(refreshToken));
    }

    @Test
    void testValidateRefreshToken_invalid() {
        assertFalse(tokenService.validateRefreshToken(null));
        assertFalse(tokenService.validateRefreshToken("invalid"));
    }

    @Test
    void testGetAccessTokenFromRefreshToken() {
        String refreshToken = tokenService.getRefreshToken(Jwts.claims().setSubject("0"), new User());
        String newAccessToken = tokenService.getAccessTokenFromRefreshToken(refreshToken);

        assertTrue(tokenService.validate(newAccessToken));
        assertEquals("0", tokenService.getIdFromToken(newAccessToken));
    }
}