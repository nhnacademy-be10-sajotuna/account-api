package com.sajotuna.account.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.domain.request.LoginRequestUser;
import com.sajotuna.account.domain.response.LoginResponse;
import com.sajotuna.account.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JsonUserIdPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Environment env;
    private final byte[] secretKey;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static Long ACCESS_TOKEN_EXPIRES = 1800 * 1000L;
    private final static Long REFRESH_TOKEN_EXPIRES = 24 * 60 * 60 * 1000L;

    public JsonUserIdPasswordAuthenticationFilter(UserService userService, Environment env, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.env = env;
        this.redisTemplate = redisTemplate;
        this.secretKey = env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8);
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (request.getContentType() == null || !request.getContentType().contains("application/json")) {
            throw new AuthenticationServiceException("Authentication method not supported");
        }

        try {
            LoginRequestUser loginRequestUser = objectMapper.readValue(request.getInputStream(), LoginRequestUser.class);

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    loginRequestUser.getEmail(), loginRequestUser.getPassword());

            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new UsernameNotFoundException("");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDto = userService.getUserByEmail(username);

        Claims claims = Jwts.claims();
        claims.put("email", userDto.getEmail());
        claims.put("name", userDto.getName());

        userService.updateLastLogin(username);

        String accessToken = getToken(claims, userDto, ACCESS_TOKEN_EXPIRES);

        String refreshToken = getToken(claims, userDto, REFRESH_TOKEN_EXPIRES);

        ResponseCookie accessTokenCookie = getResponseCookie("access_token", accessToken, ACCESS_TOKEN_EXPIRES);

        ResponseCookie refreshTokenCookie = getResponseCookie("refresh_token", refreshToken, REFRESH_TOKEN_EXPIRES);

        saveRefreshToken(userDto.getEmail(), refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        objectMapper.writeValue(response.getOutputStream(), new LoginResponse(accessToken, refreshToken, userDto.getEmail(), userDto.getName()));

    }

    private ResponseCookie getResponseCookie(String tokenName, String token, Long tokenExpires) {
        ResponseCookie accessTokenCookie = ResponseCookie.from(tokenName, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(tokenExpires)
                .sameSite("Lax")
                .build();
        return accessTokenCookie;
    }

    private String getToken(Claims claims, UserDto userDto, Long tokenExpires) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDto.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenExpires))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        if (failed instanceof AuthenticationServiceException) {
            handleException(response, failed.getMessage(), HttpStatus.BAD_REQUEST);
        } else if (failed instanceof BadCredentialsException) {
            handleException(response, failed.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        else {
            handleException(response, failed.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");

        String json = String.valueOf(ResponseEntity.status(status).body(message));
        response.getWriter().write(json);
    }
    
    private void saveRefreshToken(String email, String refreshToken){
        String key = "refresh_token:" + email;
        redisTemplate.opsForValue().set(key, refreshToken, 1, TimeUnit.DAYS);
    }

    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

}
