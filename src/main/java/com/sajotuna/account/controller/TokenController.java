package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.response.ResponseAccessToken;
import com.sajotuna.account.domain.response.ResponseUser;
import com.sajotuna.account.service.TokenService;
import com.sajotuna.account.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {
    private final TokenService tokenService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping("/validate")
    public ResponseEntity<ResponseUser> validateToken(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        if (tokenService.validate(accessToken)) {
            Long id = Long.valueOf(tokenService.getIdFromToken(accessToken));
            ResponseUser responseUser = objectMapper.convertValue(userService.getUserById(id), ResponseUser.class);
            return ResponseEntity.status(HttpStatus.OK).body(responseUser);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<ResponseAccessToken> refresh(@RequestHeader("AuthorizationRefresh") String token) {
        String refreshToken = token.substring(7);
        if (tokenService.validateRefreshToken(refreshToken)) {
            String accessToken = tokenService.getAccessTokenFromRefreshToken(refreshToken);
            Long id = Long.valueOf(tokenService.getIdFromToken(accessToken));
            ResponseUser responseUser = objectMapper.convertValue(userService.getUserById(id), ResponseUser.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseAccessToken(accessToken, responseUser));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }

}
