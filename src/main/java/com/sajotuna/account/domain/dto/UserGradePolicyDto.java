package com.sajotuna.account.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.sajotuna.account.domain.response.ResponseUserGradePolicy.Grade;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class UserGradePolicyDto {
    private Grade grade;
    private int minTotalOrderPrice;
    private int maxTotalOrderPrice;
    private int pointRate;
}
