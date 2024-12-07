package com.nbloi.cqrses.query.service.kafkaproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderCreatedEventProducer {

    private static final String TOPIC = "order_created_events";
    private final static long sendTimeout = 3000;

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderEvent(@Payload OrderCreatedEvent orderEvent) {
        try {
            kafkaTemplate.send(TOPIC, orderEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("Sent order event to kafka topic: " + TOPIC + " with record value: " + orderEvent.toString());
        } catch (Exception e) {
            log.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }
}