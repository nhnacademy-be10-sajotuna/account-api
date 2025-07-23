package com.sajotuna.account.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class RequestAddress {
    private String nickName;
    @NotNull
    private String streetAddress;
}
