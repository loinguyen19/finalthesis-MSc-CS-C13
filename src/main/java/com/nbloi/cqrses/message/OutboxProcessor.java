package com.nbloi.cqrses.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OutboxMessage;
import com.nbloi.cqrses.query.entity.Payment;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.PaymentRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
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
import java.util.Objects;

import static com.nbloi.cqrses.commonapi.enums.EventType.ORDER_CREATED_EVENT;

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

    @Transactional
    @Scheduled(fixedRate = 5000)  // Poll every 5 seconds
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxRepository.findPendingMessages();
        log.info("Message persisted in Outbox Message table: {}", messages);
        for (OutboxMessage message : messages) {
            try {
                // Publish message to Kafka
                if (Objects.equals(message.getStatus(), "PENDING")) {
                    if (Objects.equals(message.getEventType(), EventType.ORDER_CREATED_EVENT.toString())) {
                        orderCreatedEventProducer.sendOrderEvent(message.getPayload());
                        // Mark message as PROCESSED
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                    } else if (Objects.equals(message.getEventType(), EventType.PRODUCT_INVENTORY_UPDATED_EVENT.toString())) {
                        OrderCreatedEvent orderCreatedEvent = new ObjectMapper().readValue(message.getPayload(), OrderCreatedEvent.class);

                        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent();
                        paymentCreatedEvent.setPaymentId(orderCreatedEvent.getPaymentId());
                        paymentCreatedEvent.setOrderId(orderCreatedEvent.getOrderId());
                        paymentCreatedEvent.setPaymentStatus(EventType.PAYMENT_CREATED_EVENT.toString());
                        paymentCreatedEvent.setTotalAmount(orderCreatedEvent.getTotalAmount());
                        paymentCreatedEvent.setCurrency(orderCreatedEvent.getCurrency());
                        String paymentCreatedEventPayload = new ObjectMapper().writeValueAsString(paymentCreatedEvent);

                        paymentEventProducer.sendPaymentCreatedEvent(paymentCreatedEventPayload);
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                    }
                    else if (Objects.equals(message.getEventType(), EventType.PAYMENT_COMPLETED_EVENT.toString())) {
                        PaymentCompletedEvent paymentCompletedEvent = new ObjectMapper().readValue(message.getPayload(), PaymentCompletedEvent.class);

                        String paymentCompletedEventPayload = new ObjectMapper().writeValueAsString(paymentCompletedEvent);
                        paymentEventProducer.sendPaymentCompletedEvent(paymentCompletedEventPayload);
                        log.info("Produce PaymentCompletedEvent to topics payment_completed_events: {}", message.getPayload());

                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);

                    } else if (Objects.equals(message.getEventType(), EventType.PAYMENT_FAILED_EVENT.toString())) {
                        PaymentFailedEvent paymentFailedEvent = new ObjectMapper().readValue(message.getPayload(), PaymentFailedEvent.class);
                        String paymentFailedEventPayload = new ObjectMapper().writeValueAsString(paymentFailedEvent);
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);

                        paymentEventProducer.sendPaymentFailedEvent(paymentFailedEventPayload);
                        log.info("Produce PaymentFailedEvent to topics payment_failed_events: {}", messages);
                        throw new RuntimeException("Your total balance is not sufficient to pay this event");

                    } else if (Objects.equals(message.getEventType(), EventType.ORDER_CONFIRMED_EVENT.toString())) {
                        orderCreatedEventProducer.sendOrderConfirmedEvent(message.getPayload());
                        log.info("Produce OrderConfirmedEvent to topics order_confirmed_events: {}", messages);

                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                    }
                    else if (Objects.equals(message.getEventType(), EventType.ORDER_SHIPPED_EVENT.toString())) {
                        orderCreatedEventProducer.sendOrderShippedEvent(message.getPayload());
                        log.info("Produce OrderShippedEvent to topics order_shipped_events: {}", messages);

                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                    }
                    else if (Objects.equals(message.getEventType(), EventType.ORDER_CANCELLED_EVENT.toString())) {
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                    }
                    else if (Objects.equals(message.getEventType(), EventType.PAYMENT_SUMMARIZED_EVENT.toString())){
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                    }

                    log.info("Message with event type {}, status {} persisted in Outbox Message table: {}",
                            message.getEventType(), message.getStatus() ,message.getPayload());
                }
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
            return;
        }
        message.setUpdatedAt(LocalDateTime.now());
        outboxRepository.save(message);
    }
}
