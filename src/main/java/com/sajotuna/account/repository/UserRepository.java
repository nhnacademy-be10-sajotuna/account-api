package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);
    Optional<User> findByEmailAndStatusNot(String username, User.Status status);
    Optional<List<User>> findByStatusAndCurrentLoginAtLessThan(User.Status status, LocalDateTime date);

}
