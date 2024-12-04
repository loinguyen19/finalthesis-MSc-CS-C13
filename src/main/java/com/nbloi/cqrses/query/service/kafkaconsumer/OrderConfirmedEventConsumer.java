package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;

public class OrderConfirmedEventConsumer {

    @KafkaListener(topics = "payment_events", groupId = "payment_group")
    public void handleOrderConfirmedEvent(OrderConfirmedEvent orderConfirmedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Event: " + orderConfirmedEvent);
        // Implement the logic for order confirmation processing
    }
}
