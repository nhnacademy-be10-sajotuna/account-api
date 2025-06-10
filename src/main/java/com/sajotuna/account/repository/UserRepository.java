package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String username);
}
