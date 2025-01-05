package com.nbloi.conventional.eda.service.kafkaproducer;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CustomerEventProducer {

    private static final String TOPIC = "customer_deleted_events";
    private final static long sendTimeout = 3000;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CustomerEventProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendCustomerDeletedEvent(@Payload String customerDeletedEvent) {
        try {
            kafkaTemplate.send(TOPIC, customerDeletedEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            LOGGER.info("Sent Customer Deleted Event to kafka topic: " + TOPIC + " with record value: {}", customerDeletedEvent);
        } catch (Exception e) {
            LOGGER.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }

}