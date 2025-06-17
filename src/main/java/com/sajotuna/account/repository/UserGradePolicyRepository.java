package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.UserGradePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGradePolicyRepository extends JpaRepository<UserGradePolicy, Long> {
}
