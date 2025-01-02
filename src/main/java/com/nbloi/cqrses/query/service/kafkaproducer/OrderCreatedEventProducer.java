package com.nbloi.cqrses.query.service.kafkaproducer;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class OrderCreatedEventProducer {

    private static final String TOPIC = "order_created_events";
    private static final String TOPIC2 = "order_confirmed_events";
    private static final String TOPIC3 = "order_shipped_events";
    private final static long sendTimeout = 3000;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OrderCreatedEventProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderEvent(@Payload String orderEvent) {
        try {
            kafkaTemplate.send(TOPIC, orderEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            LOGGER.info("Sent order event to kafka topic: " + TOPIC + " with record value: {}", orderEvent);
        } catch (Exception e) {
            LOGGER.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }

    public void sendOrderConfirmedEvent(@Payload String orderConfirmedEvent) {
        try {
            kafkaTemplate.send(TOPIC2, orderConfirmedEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            LOGGER.info("Sent order confirmed event to kafka topic: " + TOPIC + " with record value: {}", orderConfirmedEvent);
        } catch (Exception e) {
            LOGGER.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }

    public void sendOrderShippedEvent(@Payload String orderShippedEvent) {
        try {
            kafkaTemplate.send(TOPIC3, orderShippedEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            LOGGER.info("Sent order shipped event to kafka topic: " + TOPIC + " with record value: {}", orderShippedEvent);
        } catch (Exception e) {
            LOGGER.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }
}