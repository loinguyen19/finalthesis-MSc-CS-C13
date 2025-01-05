package com.nbloi.conventional.eda.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.entity.OrderItem;
import com.nbloi.conventional.eda.entity.Product;
import com.nbloi.conventional.eda.enums.EventType;
import com.nbloi.conventional.eda.event.OrderCreatedEvent;
import com.nbloi.conventional.eda.event.PaymentCreatedEvent;
import com.nbloi.conventional.eda.exception.UnfoundEntityException;
import com.nbloi.conventional.eda.repository.PaymentRepository;
import com.nbloi.conventional.eda.repository.ProductRepository;
import com.nbloi.conventional.eda.service.ProductEventHandler;
import com.nbloi.conventional.eda.service.kafkaproducer.PaymentEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductInventoryEventConsumer {

    @Autowired
    private ProductEventHandler productInventoryEventHandler;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "order_created_events", groupId = "product_group")
    public void handleProductInventoryEvent(@Payload String orderCreatedEvent) {
        // Process the order event, e.g., store it in the database
        log.info("Received Order Event: " + orderCreatedEvent);

        // Implement the logic for inventory updating
        try {
            OrderCreatedEvent event = new ObjectMapper().readValue(orderCreatedEvent, OrderCreatedEvent.class);
            List<OrderItem> orderItems = event.getOrderItems();

            for (OrderItem o : orderItems) {
                Product productFoundById = productInventoryEventHandler.readProductById(o.getProduct().getProductId());
                if (productFoundById.equals(new Product())) {
                    throw new UnfoundEntityException(o.getProduct().getProductId(), "Product");
                } else {
                    productFoundById.setStock(productFoundById.getStock() - o.getQuantity());
                    // Save the update stock of each product
                    productRepository.save(productFoundById);
                }
            }

            // Convert OrderCreatedEvent to PaymentCreatedEvent
            PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent();
            paymentCreatedEvent.setPaymentId(event.getPaymentId());
            paymentCreatedEvent.setOrderId(event.getOrderId());
            paymentCreatedEvent.setPaymentStatus(EventType.PAYMENT_CREATED_EVENT.toString());
            paymentCreatedEvent.setTotalAmount(event.getTotalAmount());
            paymentCreatedEvent.setCurrency(event.getCurrency());

            String paymentCreatedEventPayload = new ObjectMapper().writeValueAsString(paymentCreatedEvent);
            log.info("Converting Order Created Event to Payment Created Event with PaymentID: {}", paymentCreatedEventPayload);

            // Send PaymentCreatedEvent to Kafka broker
            paymentEventProducer.sendPaymentCreatedEvent(paymentCreatedEventPayload);
            log.info("Send PaymentCreatedEvent to Kafka broker with PaymentCreatedEvent payload: {}", paymentCreatedEventPayload);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
