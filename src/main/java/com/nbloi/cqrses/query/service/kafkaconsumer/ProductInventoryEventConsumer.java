package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.event.order.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.*;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import com.nbloi.cqrses.query.service.ProductEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductInventoryEventConsumer {

    @Autowired
    private ProductEventHandler productInventoryEventHandler;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private OrderEventHandler orderEventHandler;

    @KafkaListener(topics = "order_created_events", groupId = "product_group")
    public void handleProductInventoryEvent(@Payload String orderCreatedEvent) {
        // Process the order event, e.g., store it in the database
        log.info("Received Order Event: {}", orderCreatedEvent);

        // Implement the logic for inventory updating
        try {
            OrderCreatedEvent event = new ObjectMapper().readValue(orderCreatedEvent, OrderCreatedEvent.class);
            productInventoryEventHandler.updateProductInventory(event);
                    log.info("Handle Updating Product Inventory Event: {}", event);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "payment_failed_events", groupId = "product_group")
    public void revertProductInventoryEvent(@Payload String paymentFailedEvent) {
        try {
            PaymentFailedEvent paymentEvent = new ObjectMapper().readValue(paymentFailedEvent, PaymentFailedEvent.class);
            productInventoryEventHandler.revertProductBalance(paymentEvent);
            log.info("Received PaymentFailedEvent payload: {} to revert Product balance to lastest state", paymentEvent);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
