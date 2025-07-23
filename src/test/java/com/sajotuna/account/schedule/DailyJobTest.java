package com.sajotuna.account.schedule;

import com.sajotuna.account.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DailyJobTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private DailyJob dailyJob;

    @Test
    void inactiveUsers() {

        dailyJob.inactiveUsers();

        Mockito.verify(userService, Mockito.times(1)).sleepUser();
    }
}