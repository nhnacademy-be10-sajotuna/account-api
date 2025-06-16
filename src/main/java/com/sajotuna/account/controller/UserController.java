package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.request.RequestUser;
import com.sajotuna.account.domain.response.ResponseUser;
import com.sajotuna.account.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequestUser requestUser) {
        UserDto userDto = objectMapper.convertValue(requestUser, UserDto.class);
        UserDto savedUser = userService.createUser(userDto, requestUser.getAddress());
        ResponseUser responseUser = objectMapper.convertValue(savedUser, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseUser> getUser(@RequestHeader("X-User-Id")Long userId) {
        UserDto user = userService.getUserById(userId);
        ResponseUser responseUser = objectMapper.convertValue(user, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@RequestHeader("X-User-Id")Long userId, HttpServletResponse httpServletResponse) {
        userService.deleteUser(userId);
        Cookie accessToken = new Cookie("access_token", null);
        accessToken.setMaxAge(0);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        accessToken.setPath("/");
        httpServletResponse.addCookie(accessToken);

        Cookie refreshToken = new Cookie("refresh_token", null);
        refreshToken.setMaxAge(0);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        refreshToken.setPath("/");
        httpServletResponse.addCookie(refreshToken);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-User-Id")Long userId, HttpServletResponse httpServletResponse) {
        userService.logout(userId);
        Cookie accessToken = new Cookie("access_token", null);
        accessToken.setMaxAge(0);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        accessToken.setPath("/");
        httpServletResponse.addCookie(accessToken);

        Cookie refreshToken = new Cookie("refresh_token", null);
        refreshToken.setMaxAge(0);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        refreshToken.setPath("/");
        httpServletResponse.addCookie(refreshToken);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/sleep")
    public ResponseEntity<Void> sleepUsers() {
        userService.sleepUser();
        return ResponseEntity.noContent().build();
    }

}
