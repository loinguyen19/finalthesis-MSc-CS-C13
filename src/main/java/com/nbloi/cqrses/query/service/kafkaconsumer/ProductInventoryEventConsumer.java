package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.event.order.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.*;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import com.nbloi.cqrses.query.service.ProductEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class ProductInventoryEventConsumer {

    @Autowired
    private ProductEventHandler productInventoryEventHandler;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxRepository outboxRepository;

    @KafkaListener(topics = "order_created_events", groupId = "product_group")
    public void handleProductInventoryEvent(@Payload String orderCreatedEvent) {
        // Process the order event, e.g., store it in the database
        log.info("Received Order Event: {}", orderCreatedEvent);

        // Implement the logic for inventory updating
        try {
            OrderCreatedEvent event = new ObjectMapper().readValue(orderCreatedEvent, OrderCreatedEvent.class);
//            Order order = orderRepository.findById(event.getOrderId()).orElseThrow(() ->
//                    new UnfoundEntityException(event.getOrderId(), Order.class.getSimpleName()));

            List<OrderItem> orderItems = event.getOrderItems();
//            Set<OrderItem> updatedOrderItemList = new HashSet<>();
            for (OrderItem o : orderItems) {
                Product productFoundById = productInventoryEventHandler.handle(new FindProductByIdQuery(o.getProduct().getProductId()));
//                Product product = o.getProduct();
                if (productFoundById == null) {
                    throw new UnfoundEntityException(o.getProduct().getProductId(), "Product");
                }
                else {
                    productFoundById.setStock(productFoundById.getStock() - o.getQuantity());
                    // Save the update stock of each product
                    productRepository.save(productFoundById);
                    // Save updated stock product back to order item
//                    o.setProduct(productFoundById);
//                    updatedOrderItemList.add(o);
                    // Convert OrderCreatedEvent to PaymentCreatedEvent
                    log.info("Completely updating product inventory with product id: {}", productFoundById.getProductId());
                }
            }
//            order.setOrderItems(updatedOrderItemList);
//            orderRepository.save(order);

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
            log.error(e.getMessage(), e);
            log.info("Handle Updating Product Inventory Event: {} ", e.getMessage());
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
