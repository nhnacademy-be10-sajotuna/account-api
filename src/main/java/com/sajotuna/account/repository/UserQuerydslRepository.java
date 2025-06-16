package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.User;

import java.util.List;

public interface UserQuerydslRepository {
    List<User> findUsersWithBirthdayInThisMonth();
}
