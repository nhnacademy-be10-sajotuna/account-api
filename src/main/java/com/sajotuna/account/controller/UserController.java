package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.entity.Address;
import com.sajotuna.account.domain.request.RequestUser;
import com.sajotuna.account.domain.response.ResponseUser;
import com.sajotuna.account.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser requestUser) {
        UserDto userDto = objectMapper.convertValue(requestUser, UserDto.class);
        UserDto savedUser = userService.createUser(userDto);
        ResponseUser responseUser = objectMapper.convertValue(savedUser, ResponseUser.class);
        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
    }


}
