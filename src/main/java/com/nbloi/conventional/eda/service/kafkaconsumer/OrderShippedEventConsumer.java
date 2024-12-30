package com.nbloi.conventional.eda.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.event.OrderConfirmedEvent;
import com.nbloi.conventional.eda.event.OrderShippedEvent;
import com.nbloi.conventional.eda.entity.Order;
import com.nbloi.conventional.eda.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderShippedEventConsumer {

    @Autowired
    private OrderEventHandler orderEventHandler;

    @KafkaListener(topics = "order_confirmed_events", groupId = "order_group")
    public void handleOrderShippedEvent(@Payload String orderConfirmedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Order Confirmed Event: " + orderConfirmedEvent);

        // Implement the logic for order confirmation processing
        try{
            OrderConfirmedEvent event = new ObjectMapper().readValue(orderConfirmedEvent, OrderConfirmedEvent.class);
            Order orderConfirmed = orderEventHandler.readOrderById(event.getOrderId());

            if (orderConfirmed == null) { throw new RuntimeException("No order found by id " + event.getOrderId()); }
            OrderShippedEvent orderToShipEvent = new OrderShippedEvent(orderConfirmed.getOrderId());

            orderEventHandler.on(orderToShipEvent);

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }
}
