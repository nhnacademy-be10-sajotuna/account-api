package com.sajotuna.account.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private LocalDateTime birthDate;
    private LocalDateTime createdAt;
    private long point;
    private LocalDateTime currentLoginAt;
}
