package com.sajotuna.account.domain.dto;

import com.sajotuna.account.domain.entity.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private long id;
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private User.Status status;
    private User.AuthType authType;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime currentLoginAt;
}
