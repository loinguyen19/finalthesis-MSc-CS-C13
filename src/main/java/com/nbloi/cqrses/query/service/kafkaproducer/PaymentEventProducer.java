package com.nbloi.cqrses.query.service.kafkaproducer;

import com.nbloi.cqrses.commonapi.event.PaymentEvent;
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

    public void sendPaymentEvent(@Payload PaymentEvent paymentEvent) {
        try {
            kafkaTemplate.send(TOPIC, paymentEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("Sent payment event to kafka topic: " + TOPIC + " with record value: {}", paymentEvent.toString());
        } catch (Exception e) {
            log.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }
}
