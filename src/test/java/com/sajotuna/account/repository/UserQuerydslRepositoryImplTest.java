package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserQuerydslRepositoryImplTest {
    @Autowired
    private UserRepository userRepository; // QueryDSL 구현 포함

    @Test
    void findUsersWithBirthdayInThisMonth() {
        // given
        int thisMonth = LocalDate.now().getMonthValue();

        // 생일이 이번 달인 사용자
        User user1 = new User("test1@example.com", "test1", "pw", "nick",
                LocalDate.of(1990, thisMonth, 10),
                User.Role.USER, User.AuthType.PAYCO,
                LocalDateTime.now(), User.Status.ACTIVE);
        userRepository.save(user1);

        // 생일이 다른 달인 사용자
        int otherMonth = thisMonth == 1 ? 2 : thisMonth - 1;
        User user2 = new User("test2@example.com", "test2", "pw", "nick",
                LocalDate.of(1992, otherMonth, 15),
                User.Role.USER, User.AuthType.PAYCO,
                LocalDateTime.now(), User.Status.ACTIVE);
        userRepository.save(user2);

        // when
        List<User> result = userRepository.findUsersWithBirthdayInThisMonth();

        // then
        assertThat(result)
                .hasSize(1)
                .extracting(User::getEmail)
                .containsExactly("pw");
    }
}