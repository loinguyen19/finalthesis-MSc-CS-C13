package com.nbloi.cqrses.query.service.kafkaconsumer.orderconsumer;


import com.nbloi.cqrses.commonapi.event.order.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatedEventConsumer {

    @KafkaListener(topics = "order_created_events", groupId = "order_group")
    public void handleOrderCreatedEvent(@Payload OrderCreatedEvent orderEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Order Event: " + orderEvent);
        // Implement the logic for order processing and updating inventory
    }
}