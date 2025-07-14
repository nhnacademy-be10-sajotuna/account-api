package com.sajotuna.account.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WelcomeCouponRequest {
    @NotNull
    private Long userId;
}
