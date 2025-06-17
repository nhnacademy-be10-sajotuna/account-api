package com.sajotuna.account.domain.entity;

import com.sajotuna.account.domain.dto.UserGradePolicyDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "user_grade_policy")
public class UserGradePolicy {
    @Id
    private long id;
    @NotNull
    private int minAmount;
    @NotNull
    private int maxAmount;
    @Column(precision=4, scale=3)
    @NotNull
    private BigDecimal pointEarningRate;
}
