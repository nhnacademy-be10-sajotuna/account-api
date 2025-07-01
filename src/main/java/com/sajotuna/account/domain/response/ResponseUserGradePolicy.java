package com.sajotuna.account.domain.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ResponseUserGradePolicy {
    private Grade grade;
    private int minTotalOrderPrice;
    private int maxTotalOrderPrice;
    private int pointRate;

    public enum Grade {
        GENERAL,
        ROYAL,
        GOLD,
        PLATINUM
    }
}
