package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.nbloi.cqrses.commonapi.event.InventoryEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;

public class InventoryEventConsumer {

    @KafkaListener(topics = "order_created_events", groupId = "order_group")
    public void handleInventoryEvent(InventoryEvent inventoryEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Order Event: " + inventoryEvent);
        // Implement the logic for inventory updating
    }
}
