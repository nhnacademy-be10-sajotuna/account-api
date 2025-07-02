package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.dto.UserGradePolicyDto;
import com.sajotuna.account.domain.request.LoginRequestUser;
import com.sajotuna.account.domain.request.RequestEditUser;
import com.sajotuna.account.domain.request.RequestOauth2;
import com.sajotuna.account.domain.request.RequestUser;
import com.sajotuna.account.domain.response.LoginResponse;
import com.sajotuna.account.domain.response.ResponseUser;
import com.sajotuna.account.domain.response.ResponseUserWithPolicy;
import com.sajotuna.account.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequestUser requestUser) {
        LoginResponse loginResponse = userService.login(requestUser.getEmail(), requestUser.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @PostMapping("/oauth2/{outId}")
    public ResponseEntity<LoginResponse> oauth2(@PathVariable String outId, @RequestBody RequestOauth2 requestOauth2) {
        LoginResponse loginResponse = userService.oauth2Login(outId, requestOauth2.getEmail(), requestOauth2.getName());
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @PostMapping
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequestUser requestUser) {
        UserDto userDto = UserDto.fromRequestUserLocal(requestUser);
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

    @GetMapping("/detail")
    public ResponseEntity<ResponseUserWithPolicy> getUserWithPolicy(@RequestHeader("X-User-Id")Long userId) {
        UserDto user = userService.getUserDetailById(userId);
        ResponseUserWithPolicy responseUser = objectMapper.convertValue(user, ResponseUserWithPolicy.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@RequestHeader("X-User-Id")Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateUser(@RequestHeader("X-User-Id")Long userId, @Valid @RequestBody RequestEditUser editUser) {
        UserDto userDto = objectMapper.convertValue(editUser, UserDto.class);
        userService.updateUser(userId, userDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-User-Id")Long userId) {
        userService.logout(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/birth")
    public ResponseEntity<List<ResponseUser>> getAllBirthUsers() {
        List<ResponseUser> responseUsers = new ArrayList<>();
        List<UserDto> users = userService.getUserByBirth();
        for (UserDto user : users) {
            ResponseUser responseUser = objectMapper.convertValue(user, ResponseUser.class);
            responseUsers.add(responseUser);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseUsers);
    }
}
