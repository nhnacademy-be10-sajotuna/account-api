package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);
    Optional<User> findByEmailAndStatusNot(String username, User.Status status);
}
