package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.QUser;
import com.sajotuna.account.domain.entity.User;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.util.List;

public class UserQuerydslRepositoryImpl extends QuerydslRepositorySupport implements UserQuerydslRepository {
    public UserQuerydslRepositoryImpl() {
        super(User.class);
    }

    public List<User> findUsersWithBirthdayInThisMonth() {
        QUser user = QUser.user;

        int currentMonth = LocalDate.now().getMonthValue();

        return from(user)
                .where(user.birthDate.month().eq(currentMonth))
                .fetch();
    }
}
