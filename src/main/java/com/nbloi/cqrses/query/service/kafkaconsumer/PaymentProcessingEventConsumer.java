package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import com.nbloi.cqrses.query.service.kafkaproducer.PaymentEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessingEventConsumer {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventHandler orderEventHandler;

    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "order_created_events", groupId = "payment_group")
    public void handlePaymentEvent(@Payload PaymentCompletedEvent paymentCompletedEvent) {
        // Process the payment event, e.g., update payment status
        System.out.println("Received Payment Event: " + paymentCompletedEvent);
        // Implement the logic for payment processing and order status update

        OrderConfirmedEvent orderConfirmedEvent;
        try {
            orderConfirmedEvent = new ObjectMapper().convertValue(paymentCompletedEvent, OrderConfirmedEvent.class);
            System.out.println(orderConfirmedEvent);

            if (!orderConfirmedEvent.getOrderId().isEmpty()) {
                Order order = orderEventHandler.handle(new FindOrderByIdQuery(orderConfirmedEvent.getOrderId()));
                order.setOrderConfirmedStatus();
                orderRepository.save(order);
            } else {
                throw new UnfoundEntityException(orderConfirmedEvent.getOrderId(), "Order");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
