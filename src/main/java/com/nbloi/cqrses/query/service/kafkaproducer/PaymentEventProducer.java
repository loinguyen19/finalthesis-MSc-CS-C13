package com.nbloi.cqrses.query.service.kafkaproducer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PaymentEventProducer {

    private static final String TOPIC = "payment_created_events";
    private static final String TOPIC2 = "payment_completed_events";
    private final static long sendTimeout = 3000;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendPaymentCreatedEvent(@Payload String paymentEvent) {
        try {
            // Send this PaymentCreatedEvent object to Kafka consumer
            kafkaTemplate.send(TOPIC, paymentEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("Sent payment event to kafka topic: " + TOPIC + " with record value: {}", paymentEvent);
        } catch (Exception e) {
            log.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }

    public void sendPaymentCompletedEvent(@Payload String paymentCompletedEvent) {
        try {
            // Send this PaymentCompletedEvent object to Kafka consumer
            kafkaTemplate.send(TOPIC2, paymentCompletedEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("Sent payment completed event to kafka topic: " + TOPIC + " with record value: {}", paymentCompletedEvent);
        } catch (Exception e) {
            log.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }
}
