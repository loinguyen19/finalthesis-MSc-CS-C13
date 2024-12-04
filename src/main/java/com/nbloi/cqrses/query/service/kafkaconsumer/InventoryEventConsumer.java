package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.InventoryEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.query.entity.InventoryDetails;
import com.nbloi.cqrses.query.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventConsumer {

    @Autowired
    InventoryRepository inventoryRepository;

    @KafkaListener(topics = "order_created_events", groupId = "order_group")
    public void handleInventoryEvent(OrderCreatedEvent orderCreatedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Order Event: " + orderCreatedEvent);
        // Implement the logic for inventory updating

        InventoryEvent inventoryEvent;
        try {
            inventoryEvent = new ObjectMapper().convertValue(orderCreatedEvent, InventoryEvent.class);
            System.out.println(inventoryEvent);

            InventoryDetails inventoryDetail = new ObjectMapper().convertValue(inventoryEvent, InventoryDetails.class);
            InventoryDetails inventoryObjToUpdate = inventoryRepository.findById(inventoryDetail.getInventoryId()).get();

            // Update the quantity of inventory by id
            inventoryObjToUpdate.setQuantity(inventoryObjToUpdate.getQuantity() - inventoryDetail.getQuantity());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
