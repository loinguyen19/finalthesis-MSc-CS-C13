package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

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
            Product productFoundById = productRepository.findById(orderCreatedEvent.getProductId()).get();
            if (productFoundById.equals(new Product())) {
                throw new RuntimeException("No product found with id " + orderCreatedEvent.getProductId());
            }

//            // Update the quantity of product by id
//            if (productFoundById.getStock() < orderCreatedEvent.getQuantity()) {
//                throw new OutOfProductStockException();
//            }else {
                productFoundById.setStock(productFoundById.getStock() - orderCreatedEvent.getQuantity());

                // Save the update stock of each product
                productRepository.save(productFoundById);

            System.out.println(productFoundById);



        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
