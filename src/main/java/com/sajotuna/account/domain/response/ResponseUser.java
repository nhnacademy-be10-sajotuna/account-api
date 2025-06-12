package com.sajotuna.account.domain.response;

import com.sajotuna.account.domain.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseUser {
    private long id;

    private long policyId;

    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private User.Status status;
    private User.AuthType authType;
    private LocalDateTime currentLoginAt;
}
