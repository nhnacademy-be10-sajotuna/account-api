package com.sajotuna.account.schedule;

import com.sajotuna.account.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyJob {

    private final UserService userService;

    @Scheduled(cron = "0 1 0 * * *")
    public void inactiveUsers() {
        log.info("3달간 로그인 안한 유저 상태 변환: {}", LocalDateTime.now());
        userService.sleepUser();
    }
}
