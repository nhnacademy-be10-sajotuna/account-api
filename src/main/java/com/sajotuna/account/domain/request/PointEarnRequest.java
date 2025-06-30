package com.sajotuna.account.domain.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PointEarnRequest {
    private Long userId;
    private PointPolicyType type;

    public enum PointPolicyType {
        REGISTER
    }
}
