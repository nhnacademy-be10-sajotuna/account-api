package com.sajotuna.account.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class WelcomeCouponRequest {
    @NotNull
    private Long userId;
}
