package com.sajotuna.account.domain.response;

import com.sajotuna.account.domain.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public class ResponseUser {
    private long id;

    private long policyId;

    private String name;
    private String email;
    private String phoneNumber;
    private LocalDateTime birthDate;
    private LocalDateTime createdAt;
    private User.Status status;
    private User.AuthType authType;
    private LocalDateTime currentLoginAt;
}
