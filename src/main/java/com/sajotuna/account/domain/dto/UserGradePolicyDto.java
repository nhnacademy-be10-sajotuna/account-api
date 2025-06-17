package com.sajotuna.account.domain.dto;

import com.sajotuna.account.domain.entity.UserGradePolicy;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class UserGradePolicyDto {
    private int minAmount;
    private int maxAmount;
    private BigDecimal pointEarningRate;

    public UserGradePolicyDto(UserGradePolicy userGradePolicy) {
        this.minAmount = userGradePolicy.getMinAmount();
        this.maxAmount = userGradePolicy.getMaxAmount();
        this.pointEarningRate = userGradePolicy.getPointEarningRate();
    }
}
