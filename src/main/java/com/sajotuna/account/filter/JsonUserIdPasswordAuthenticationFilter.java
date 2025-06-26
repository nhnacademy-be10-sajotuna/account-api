package com.sajotuna.account.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.domain.request.LoginRequestUser;
import com.sajotuna.account.domain.response.LoginResponse;
import com.sajotuna.account.service.TokenService;
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

public class JsonUserIdPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenService tokenService;

    public JsonUserIdPasswordAuthenticationFilter(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
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
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String username = ((User) authResult.getPrincipal()).getUsername();

        UserDto userDto = userService.getUserByEmail(username);

        Claims claims = Jwts.claims();
        claims.put("email", userDto.getEmail());
        claims.put("role", userDto.getRole());

        userService.updateLastLogin(username);

        String accessToken = tokenService.getAccessToken(claims, userDto);
        String refreshToken = tokenService.getRefreshToken(claims, userDto);
        tokenService.saveRefreshToken(userDto.getEmail(), refreshToken);

        objectMapper.writeValue(response.getOutputStream(), new LoginResponse(accessToken, refreshToken, userDto.getEmail(), userDto.getName()));

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
}
