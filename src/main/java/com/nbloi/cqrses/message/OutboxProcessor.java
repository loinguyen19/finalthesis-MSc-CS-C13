package com.nbloi.cqrses.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCreatedEvent;
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
    @Autowired
    private ProductInventoryEventConsumer productInventoryEventConsumer;

    @Autowired
    private ProductInventoryEventProducer productInventoryEventProducer;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private PaymentRepository paymentRepository;

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

//                        Payment payment = paymentRepository.findById(orderCreatedEvent.getPaymentId()).orElseThrow(RuntimeException::new);

                        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent();
                        paymentCreatedEvent.setPaymentId(orderCreatedEvent.getPaymentId());
                        paymentCreatedEvent.setOrderId(orderCreatedEvent.getOrderId());
                        paymentCreatedEvent.setPaymentStatus(EventType.PAYMENT_CREATED_EVENT.toString());
                        paymentCreatedEvent.setTotalAmount(orderCreatedEvent.getTotalAmount());
                        paymentCreatedEvent.setCurrency(orderCreatedEvent.getCurrency());

                        paymentEventProducer.sendPaymentCreatedEvent(message.getPayload());
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                    }
                    else if (Objects.equals(message.getEventType(), EventType.PAYMENT_COMPLETED_EVENT.toString())) {
//                        Payment payment = paymentRepository.findById(message.getAggregateId()).get();
                        PaymentCompletedEvent paymentCompletedEvent = new ObjectMapper().readValue(message.getPayload(), PaymentCompletedEvent.class);

//                        String orderId = paymentCompletedEvent.getOrderId();
//                        OrderCreatedEvent event = new OrderCreatedEvent();
//                        event.setOrderId(orderId);
//                        event.setPaymentId(paymentCompletedEvent.getPaymentId());

                        String paymentCompletedEventPayload = new ObjectMapper().writeValueAsString(paymentCompletedEvent);
                        paymentEventProducer.sendPaymentCompletedEvent(paymentCompletedEventPayload);
                        messages.remove(message);
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);

                    } else if (Objects.equals(message.getEventType(), EventType.PAYMENT_FAILED_EVENT.toString())) {
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                        throw new RuntimeException("Your total balance is not sufficient to pay this event");

                    } else if (Objects.equals(message.getEventType(), EventType.ORDER_CONFIRMED_EVENT.toString())) {
                        orderCreatedEventProducer.sendOrderConfirmedEvent(message.getPayload());
                        message.setStatus("PROCESSED");
                        message.setUpdatedAt(LocalDateTime.now());
                        outboxRepository.save(message);
                    }
                    // Mark message as PROCESSED
//                    message.setStatus("PROCESSED");
//                    message.setUpdatedAt(LocalDateTime.now());
//                    outboxRepository.save(message);
                    log.info("Message changed to 'PROCESSED' status persisted in Outbox Message table: {}", messages);
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
