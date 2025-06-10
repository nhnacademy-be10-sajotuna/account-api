package com.sajotuna.account.domain.request;

import lombok.Data;

@Data
public class LoginRequestUser {
    private String email;
    private String password;
}
