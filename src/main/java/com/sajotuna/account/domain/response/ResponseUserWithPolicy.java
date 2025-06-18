package com.sajotuna.account.domain.response;

import com.sajotuna.account.domain.dto.UserGradePolicyDto;
import com.sajotuna.account.domain.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseUserWithPolicy {
    private long id;

    private UserGradePolicyDto userGradePolicyDto;

    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private User.Status status;
    private User.AuthType authType;
    private LocalDateTime currentLoginAt;
}
