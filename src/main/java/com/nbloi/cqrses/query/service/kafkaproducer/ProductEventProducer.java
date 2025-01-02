package com.nbloi.cqrses.query.service.kafkaproducer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProductEventProducer {

//    private static final String TOPIC = "product_inventory_updated_events";
    private static final String TOPIC = "product_updated_events";
    private final static long sendTimeout = 3000;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
//
//    public void sendProductInventoryEvent(@Payload String productEvent) {
//        try {
//            // Send this ProductEvent object to Kafka consumer
//            kafkaTemplate.send(TOPIC, productEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
//            log.info("Sent product inventory updating event to kafka topic: " + TOPIC + " with record value: {}", productEvent);
//        } catch (Exception e) {
//            log.info("KafkaEventBus publish get timeout", e);
//            throw new RuntimeException(e);
//        }
//    }

    public void sendProductDeletedEvent(@Payload String productDeletedEvent) {
        try {
            kafkaTemplate.send(TOPIC, productDeletedEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            log.info("Sent Product Deleted Event to Kafka topic: " + TOPIC + " with record value: {}", productDeletedEvent);
        } catch (Exception e) {
            log.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }
}
