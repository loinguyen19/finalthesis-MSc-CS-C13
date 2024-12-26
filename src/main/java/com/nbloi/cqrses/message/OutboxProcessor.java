package com.nbloi.cqrses.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCreatedEvent;
import com.nbloi.cqrses.query.entity.OutboxMessage;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.service.kafkaconsumer.ProductInventoryEventConsumer;
import com.nbloi.cqrses.query.service.kafkaproducer.OrderCreatedEventProducer;
import com.nbloi.cqrses.query.service.kafkaproducer.PaymentEventProducer;
import com.nbloi.cqrses.query.service.kafkaproducer.ProductInventoryEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxProcessor {

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OrderCreatedEventProducer orderCreatedEventProducer;

    private final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);
    @Autowired
    private PaymentEventProducer paymentEventProducer;
    @Autowired
    private ProductInventoryEventConsumer productInventoryEventConsumer;

    @Autowired
    private ProductInventoryEventProducer productInventoryEventProducer;

    @Transactional
    @Scheduled(fixedRate = 5000)  // Poll every 5 seconds
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxRepository.findPendingMessages();
        log.info("Message persisted in Outbox Message table: {}", messages);
        for (OutboxMessage message : messages) {
            try {
                // Publish message to Kafka
                EventType eventType = EventType.valueOf(message.getEventType());
                switch (eventType) {
                    case EventType.ORDER_CREATED_EVENT:
                        orderCreatedEventProducer.sendOrderEvent(message.getPayload());
                    case EventType.PRODUCT_INVENTORY_UPDATED_EVENT, EventType.PAYMENT_CREATED_EVENT:
                        OrderCreatedEvent orderCreatedEvent = new ObjectMapper().readValue(message.getPayload(), OrderCreatedEvent.class);
                        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent();
                        paymentCreatedEvent.setPaymentId(orderCreatedEvent.getPaymentId());
                        paymentCreatedEvent.setPaymentDate(LocalDateTime.now());
                        paymentCreatedEvent.setPaymentStatus(PaymentStatus.CREATED);
                        paymentCreatedEvent.setTotalAmount(orderCreatedEvent.getTotalAmount());
                        paymentCreatedEvent.setCurrency(orderCreatedEvent.getCurrency());
                        paymentCreatedEvent.setOrderId(orderCreatedEvent.getOrderId());

                        String paymentCreatedEventPayload = new ObjectMapper().writeValueAsString(paymentCreatedEvent);
                        paymentEventProducer.sendPaymentCreatedEvent(paymentCreatedEventPayload);
                    case EventType.PAYMENT_COMPLETED_EVENT:
                        paymentEventProducer.sendPaymentCompletedEvent(message.getPayload());
                    case EventType.PAYMENT_FAILED_EVENT:
                        throw new RuntimeException("Your total balance is not sufficient to pay this event");
                    default:
                }

                // Mark message as PROCESSED
                message.setStatus("PROCESSED");
                message.setUpdatedAt(LocalDateTime.now());
                outboxRepository.save(message);
            } catch (Exception e) {
                // Handle failures (retry mechanism)
                handleFailure(message, e);
            }
        }
    }

    private void handleFailure(OutboxMessage message, Exception e) {
        // Increment retry count or mark as FAILED after max retries
        message.incrementRetryCount();
        if (message.getRetryCount() >= 5) {  // Configurable max retries
            message.setStatus("FAILED");
        }
        message.setUpdatedAt(LocalDateTime.now());
        outboxRepository.save(message);
    }
}
