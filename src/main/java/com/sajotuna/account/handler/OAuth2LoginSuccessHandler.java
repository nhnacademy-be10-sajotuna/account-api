package com.sajotuna.account.handler;

import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.service.CookieService;
import com.sajotuna.account.service.TokenService;
import com.sajotuna.account.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final UserService userService;
    private final CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        UserDto userDto = userService.getUserByEmail(email);


        Claims claims = Jwts.claims();
        claims.put("email", userDto.getEmail());
        claims.put("role", userDto.getRole());

        userService.updateLastLogin(email);

        String refreshToken = tokenService.getRefreshToken(claims, userDto);
        String accessToken = tokenService.getAccessToken(claims, userDto);
        tokenService.saveRefreshToken(userDto.getEmail(), refreshToken);

        ResponseCookie accessTokenCookie = cookieService.getAccessTokenCookie(accessToken);
        ResponseCookie refreshTokenCookie = cookieService.getRefreshTokenCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.sendRedirect("/");
    }
}
