package com.nbloi.cqrses.query.service.kafkaconsumer.orderconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.order.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConfirmedEventConsumer {

    @Autowired
    private OrderEventHandler orderEventHandler;

    @Autowired
    private OutboxRepository outboxRepository;

    @KafkaListener(topics = "payment_completed_events", groupId = "order_group")
    public void handleOrderConfirmedEvent(@Payload String paymentCompletedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Event in OrderConfirmedEventConsumer: " + paymentCompletedEvent);

        // Implement the logic for order confirmation processing
        try{
            PaymentCompletedEvent paymentEvent = new ObjectMapper().readValue(paymentCompletedEvent, PaymentCompletedEvent.class);
            Order foundOrder = orderEventHandler.handle(new FindOrderByIdQuery(paymentEvent.getOrderId()));

            if (foundOrder == null) {
                throw new RuntimeException("No order found by id " + paymentEvent.getOrderId());
            }
            OrderConfirmedEvent orderConfirmedEvent = new OrderConfirmedEvent(foundOrder.getOrderId());
            orderEventHandler.on(orderConfirmedEvent);

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }
}
