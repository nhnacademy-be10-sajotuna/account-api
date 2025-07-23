package com.sajotuna.account.domain.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAccessToken {
    private String accessToken;
    private ResponseUser responseUser;
}
