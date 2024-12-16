package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.ProductInventoryEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductInventoryEventConsumer {

    @Autowired
    private ProductInventoryEventHandler productInventoryEventHandler;

    @Autowired
    private ProductRepository productRepository;

    @KafkaListener(topics = "order_created_events", groupId = "product_group")
    public void handleProductInventoryEvent(@Payload String orderCreatedEvent) {
        // Process the order event, e.g., store it in the database
        log.info("Received Order Event: " + orderCreatedEvent);

        // Implement the logic for inventory updating
        try {
            OrderCreatedEvent event = new ObjectMapper().readValue(orderCreatedEvent, OrderCreatedEvent.class);
            List<OrderItem> orderItems = event.getOrderItems();

            for (OrderItem o : orderItems) {
                Product productFoundById = productInventoryEventHandler.handle(new FindProductByIdQuery(o.getProduct().getProductId()));
                if (productFoundById.equals(new Product())) {
                    throw new UnfoundEntityException(o.getProduct().getProductId(), "Product");
                } else {
                    productFoundById.setStock(productFoundById.getStock() - o.getQuantity());

                    // Save the update stock of each product
                    productRepository.save(productFoundById);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
