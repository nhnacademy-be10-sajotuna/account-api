package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.request.RequestUser;
import com.sajotuna.account.domain.response.ResponseUser;
import com.sajotuna.account.exception.IllegalVariableException;
import com.sajotuna.account.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequestUser requestUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalVariableException(bindingResult.getFieldError().getDefaultMessage());
        }
        UserDto userDto = objectMapper.convertValue(requestUser, UserDto.class);
        UserDto savedUser = userService.createUser(userDto);
        ResponseUser responseUser = objectMapper.convertValue(savedUser, ResponseUser.class);
        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        ResponseUser responseUser = objectMapper.convertValue(user, ResponseUser.class);
        return new ResponseEntity<>(responseUser, HttpStatus.OK);
    }

}
