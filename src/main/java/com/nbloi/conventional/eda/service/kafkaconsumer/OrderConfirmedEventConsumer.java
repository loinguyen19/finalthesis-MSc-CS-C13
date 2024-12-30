package com.nbloi.conventional.eda.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.event.OrderConfirmedEvent;
import com.nbloi.conventional.eda.event.PaymentCompletedEvent;
import com.nbloi.conventional.eda.entity.Order;
import com.nbloi.conventional.eda.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConfirmedEventConsumer {

    @Autowired
    private OrderEventHandler orderEventHandler;

    @KafkaListener(topics = "payment_completed_events", groupId = "payment_group")
    public void handleOrderConfirmedEvent(@Payload String paymentCompletedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Event in OrderConfirmedEventConsumer: " + paymentCompletedEvent);

        // Implement the logic for order confirmation processing
        try{
            PaymentCompletedEvent paymentEvent = new ObjectMapper().readValue(paymentCompletedEvent, PaymentCompletedEvent.class);
            Order foundOrder = orderEventHandler.readOrderById(paymentEvent.getOrderId());

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
