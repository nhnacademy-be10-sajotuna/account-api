package com.sajotuna.account.domain.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
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
