package com.sajotuna.account.domain.response;

import lombok.*;

@Getter
@AllArgsConstructor
public class ResponseAccessToken {
    private String accessToken;
    private ResponseUser responseUser;
}
