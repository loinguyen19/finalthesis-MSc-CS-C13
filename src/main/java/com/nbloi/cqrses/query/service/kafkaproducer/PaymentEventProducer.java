package com.nbloi.cqrses.query.service.kafkaproducer;

import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentEvent;
import com.nbloi.cqrses.config.SerializerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PaymentEventProducer {

    private static final String TOPIC = "payment_events";
    private final static long sendTimeout = 3000;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendPaymentEvent(@Payload String paymentEvent) {
        try {
            // Deserialize the payload into PaymentEvent object
            PaymentEvent paymentEventDeSerialized = SerializerUtils.deserializeFromJsonBytes(paymentEvent, PaymentEvent.class);

            // Send this PaymentEvent object to Kafka consumer
            kafkaTemplate.send(TOPIC, paymentEventDeSerialized).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("Sent payment event to kafka topic: " + TOPIC + " with record value: {}", paymentEvent.toString());
        } catch (Exception e) {
            log.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }
}
