package com.sajotuna.account.domain.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAddress {
    private long id;
    private String nickName;
    private String streetAddress;
}
