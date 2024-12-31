package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.*;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.PaymentRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import com.nbloi.cqrses.query.service.ProductInventoryEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.DataInput;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductInventoryEventConsumer {

    @Autowired
    private ProductInventoryEventHandler productInventoryEventHandler;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private OrderEventHandler orderEventHandler;

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
                    // TODO: recheck that the product inventory has not been updated after order created successfully
                    // TODO: UPDATED - it's updated, but asynchronously
                    // Save the update stock of each product
                    productRepository.save(productFoundById);
                }
            }

            // Convert OrderCreatedEvent to PaymentCreatedEvent
            log.info("Converting Order Created Event to Payment Created Event with PaymentID: {}", event.getPaymentId());

            // Create message for outbox message
            OutboxMessage outboxMessage = new OutboxMessage(UUID.randomUUID().toString(),
                    event.getOrderId(),
                    EventType.PRODUCT_INVENTORY_UPDATED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(event),
                    OutboxStatus.PENDING.toString());

            // Send message to Outbox message queue for Product Inventory Event
            outboxRepository.save(outboxMessage);
            log.info("Processing ProductInventoryEvent OutboxMessage with PaymentCreatedEvent payload: {}", outboxMessage.getPayload());

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
