package com.sajotuna.account.service;

import com.sajotuna.account.domain.request.PointEarnRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointMessageProducerTest {
    @Mock
    RabbitTemplate rabbitTemplate;

    PointMessageProducer pointMessageProducer;

    @BeforeEach
    void setUp() {
        pointMessageProducer = new PointMessageProducer(rabbitTemplate);

        // @Value 필드 수동 주입
        ReflectionTestUtils.setField(pointMessageProducer, "exchangeName", "test-exchange");
        ReflectionTestUtils.setField(pointMessageProducer, "routingKey", "test.routing.key");
    }

    @Test
    void sendPointEarnRequest_ShouldSendMessage() {
        PointEarnRequest request = new PointEarnRequest(1L, PointEarnRequest.PointPolicyType.REGISTER);

        pointMessageProducer.sendPointEarnRequest(request);

        verify(rabbitTemplate, times(1)).convertAndSend("test-exchange", "test.routing.key", request);
    }
}