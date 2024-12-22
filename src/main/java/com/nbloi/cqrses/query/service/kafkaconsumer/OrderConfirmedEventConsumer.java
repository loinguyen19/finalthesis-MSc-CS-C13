package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConfirmedEventConsumer {

    @Autowired
    OrderEventHandler orderEventHandler;

    @KafkaListener(topics = "payment_events", groupId = "payment_group")
    public void handleOrderConfirmedEvent(@Payload PaymentCompletedEvent paymentCompletedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Event: " + paymentCompletedEvent);

        // Implement the logic for order confirmation processing
        try{
            Order orderToConfirm = orderEventHandler.handle(new FindOrderByIdQuery(paymentCompletedEvent.getOrderId()));

            if (orderToConfirm == null) { throw new RuntimeException("No order found by id " + paymentCompletedEvent.getOrderId()); }
            OrderConfirmedEvent orderConfirmedEvent = new ObjectMapper().convertValue(orderToConfirm, OrderConfirmedEvent.class);

            orderEventHandler.on(orderConfirmedEvent);

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }
}
