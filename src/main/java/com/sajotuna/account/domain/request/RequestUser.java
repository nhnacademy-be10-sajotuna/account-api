package com.sajotuna.account.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class RequestUser {
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String address;
}
