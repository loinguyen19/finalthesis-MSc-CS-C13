//package com.nbloi.cqrses.query.service.kafkaconsumer;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nbloi.cqrses.commonapi.enums.EventType;
//import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
//import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
//import com.nbloi.cqrses.commonapi.event.PaymentCreatedEvent;
//import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
//import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
//import com.nbloi.cqrses.query.entity.Order;
//import com.nbloi.cqrses.query.entity.OutboxMessage;
//import com.nbloi.cqrses.query.repository.OrderRepository;
//import com.nbloi.cqrses.query.repository.OutboxRepository;
//import com.nbloi.cqrses.query.service.OrderEventHandler;
//import com.nbloi.cqrses.query.service.PaymentEventHandler;
//import com.nbloi.cqrses.query.service.kafkaproducer.PaymentEventProducer;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Service
//@Slf4j
//public class PaymentProcessingEventConsumer {
//
//    @Autowired
//    private OutboxRepository outboxRepository;
//
//    @KafkaListener(topics = "payment_created_events", groupId = "payment_group")
//    public void handlePaymentEvent(@Payload String paymentCreatedEvent) {
//        // Process the payment event, e.g., update payment status
//        log.info("Received Payment Created Event: " + paymentCreatedEvent);
//        // Implement the logic for payment processing and order status update
//
//        OrderConfirmedEvent orderConfirmedEvent;
//        try {
//            // TODO: call onProcessing in PaymentEventHandler to update the balance of customer and call function go to confirm payment
//            // TODO: send message to outbox message. Then confirm payment is successful or failed.
//
//            PaymentCreatedEvent paymentProcessingEvent = new ObjectMapper().readValue(paymentCreatedEvent, PaymentCreatedEvent.class);
//
//            // Create message for outbox message
//            OutboxMessage outboxMessage = new OutboxMessage(UUID.randomUUID().toString(),
//                    paymentProcessingEvent.getPaymentId(),
//                    EventType.PAYMENT_CREATED_EVENT.toString(),
//                    new ObjectMapper().writeValueAsString(paymentProcessingEvent),
//                    OutboxStatus.PENDING.toString());
//
//            // Send message to Outbox message queue for Product Inventory Event
//            outboxRepository.save(outboxMessage);
//            log.info("Processing OutboxMessage with payload: {}", outboxMessage.getPayload());
//
////            orderConfirmedEvent = new ObjectMapper().convertValue(paymentCreatedEvent, OrderConfirmedEvent.class);
////            System.out.println(orderConfirmedEvent);
////
////            if (!orderConfirmedEvent.getOrderId().isEmpty()) {
////                Order order = orderEventHandler.handle(new FindOrderByIdQuery(orderConfirmedEvent.getOrderId()));
////                order.setOrderConfirmedStatus();
////                orderRepository.save(order);
////            } else {
////                throw new UnfoundEntityException(orderConfirmedEvent.getOrderId(), "Order");
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//}
