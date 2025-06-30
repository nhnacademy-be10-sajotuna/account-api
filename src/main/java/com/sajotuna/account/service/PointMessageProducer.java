package com.sajotuna.account.service;

import com.sajotuna.account.domain.request.PointEarnRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PointMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.point.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.point.routing-key}")
    private String routingKey;

    public PointMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendPointEarnRequest(PointEarnRequest request) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, request);
    }
}
