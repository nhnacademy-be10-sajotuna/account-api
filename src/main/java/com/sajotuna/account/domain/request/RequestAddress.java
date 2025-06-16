package com.sajotuna.account.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddress {
    private String nickName;
    @NotNull
    private String streetAddress;
}
