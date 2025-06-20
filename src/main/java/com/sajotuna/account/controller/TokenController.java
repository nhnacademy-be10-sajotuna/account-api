package com.sajotuna.account.controller;

import com.sajotuna.account.domain.response.ResponseAccessToken;
import com.sajotuna.account.service.TokenService;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/refresh")
public class TokenController {
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<ResponseAccessToken> refresh(@RequestHeader("AuthorizationRefresh") String token) throws Exception {
        String refreshToken = token.substring(7);
        if (!tokenService.validateRefreshToken(refreshToken)) {
            throw new ForbiddenException("Invalid refresh token");
        }
        String accessToken = tokenService.getAccessTokenFromRefreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseAccessToken(accessToken));
    }

}
