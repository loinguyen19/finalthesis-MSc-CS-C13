package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.Payment;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConfirmedEventConsumer {

    @Autowired
    OrderEventHandler orderEventHandler;

    @KafkaListener(topics = "payment_completed_events", groupId = "payment_group")
    public void handleOrderConfirmedEvent(@Payload String paymentCompletedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Event: " + paymentCompletedEvent);

        // Implement the logic for order confirmation processing
        try{
            Payment payment = new ObjectMapper().readValue(paymentCompletedEvent, Payment.class);
            Order foundOrder = orderEventHandler.handle(new FindOrderByIdQuery(payment.getOrder().getOrderId()));

            if (foundOrder == null) {
                throw new RuntimeException("No order found by id " + payment.getOrder().getOrderId());
            }
            OrderConfirmedEvent orderConfirmedEvent = new OrderConfirmedEvent(foundOrder.getOrderId());

            orderEventHandler.on(orderConfirmedEvent);
            // TODO: create sendConfirmedEvent in producer of order. Send sendConfirmedEvent() to convert to Shipped order
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }
}
