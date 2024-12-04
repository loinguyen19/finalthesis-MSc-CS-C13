package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.nbloi.cqrses.commonapi.event.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.query.entity.Products;
import com.nbloi.cqrses.query.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductInventoryEventConsumer {

    @Autowired
    ProductRepository productRepository;

    @KafkaListener(topics = "order_created_events", groupId = "order_group")
    public void handleProductInventoryEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Order Event: " + orderCreatedEvent);

        // Implement the logic for inventory updating
        try {
            Products productToSave = new Products();
            productToSave.setProductId(orderCreatedEvent.getProductId());

            Products productFoundById = productRepository.findById(orderCreatedEvent.getProductId()).get();
            if (productFoundById == null) {
                throw new RuntimeException("No product found with id " + orderCreatedEvent.getProductId());
            }
            productToSave.setName(productFoundById.getName());

            // Update the quantity of product by id
            if (orderCreatedEvent.getQuantity() > orderCreatedEvent.getQuantity()) {
                throw new OutOfProductStockException();
            }else {
                productToSave.setQuantity(productFoundById.getQuantity() - orderCreatedEvent.getQuantity());
            }

            System.out.println(productToSave);

            // Save the update stock of each product
            productRepository.save(productToSave);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
