package com.sajotuna.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "cookie.domain=test.com",
        "cookie.secure=true"
})
class CookieServiceTest {
    @InjectMocks
    private CookieService cookieService;

    @BeforeEach
    void setUp() {
        // 강제로 @Value 주입 (필드 주입이라 일반적으로 reflection 사용)
        ReflectionTestUtils.setField(cookieService, "cookieDomain", "test.com");
        ReflectionTestUtils.setField(cookieService, "cookieSecure", true);
    }

    @Test
    @DisplayName("어세스 토큰 쿠키")
    void testAccessTokenCookie() {
        String token = "abc123";
        ResponseCookie cookie = cookieService.getAccessTokenCookie(token);

        assertEquals("access_token", cookie.getName());
        assertEquals(token, cookie.getValue());
        assertEquals("test.com", cookie.getDomain());
        assertTrue(cookie.isSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(1800L, cookie.getMaxAge().getSeconds());
    }

    @Test
    @DisplayName("리프레쉬 토큰 쿠키")
    void testRefreshTokenCookie() {
        String token = "xyz789";
        ResponseCookie cookie = cookieService.getRefreshTokenCookie(token);

        assertEquals("refresh_token", cookie.getName());
        assertEquals(token, cookie.getValue());
        assertEquals("test.com", cookie.getDomain());
        assertTrue(cookie.isSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(24 * 60 * 60L, cookie.getMaxAge().getSeconds());
    }
}