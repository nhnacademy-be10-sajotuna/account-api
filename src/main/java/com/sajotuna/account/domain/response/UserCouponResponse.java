package com.sajotuna.account.domain.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCouponResponse {
    private Long userCouponId;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresDate;
    private UserCouponType userCouponType;
}
