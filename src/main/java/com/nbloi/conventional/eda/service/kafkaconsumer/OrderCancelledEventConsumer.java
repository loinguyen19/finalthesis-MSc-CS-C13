package com.nbloi.conventional.eda.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.event.OrderCancelledEvent;
import com.nbloi.conventional.eda.event.PaymentFailedEvent;
import com.nbloi.conventional.eda.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderCancelledEventConsumer {

    @Autowired
    private OrderEventHandler orderEventHandler;

    @KafkaListener(topics = "payment_failed_events", groupId = "order_group")
    public void handleOrderCancelledEvent(@Payload String paymentFailedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Failed Event: " + paymentFailedEvent);

        // Implement the logic for order confirmation processing
        try{
            PaymentFailedEvent paymentEvent = new ObjectMapper().readValue(paymentFailedEvent, PaymentFailedEvent.class);
            OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(paymentEvent.getOrderId());
            orderEventHandler.on(orderCancelledEvent);

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }
}
