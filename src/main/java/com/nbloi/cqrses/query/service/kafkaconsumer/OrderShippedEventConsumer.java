package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderShippedEventConsumer {

    @Autowired
    OrderEventHandler orderEventHandler;

    @KafkaListener(topics = "order_confirm_events", groupId = "order_group")
    public void handleOrderConfirmedEvent(@Payload String orderConfirmedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Order Confirmed Event: " + orderConfirmedEvent);

        // Implement the logic for order confirmation processing
        try{
            OrderConfirmedEvent event = new ObjectMapper().readValue(orderConfirmedEvent, OrderConfirmedEvent.class);
            Order orderConfirmed = orderEventHandler.handle(new FindOrderByIdQuery(event.getOrderId()));

            if (orderConfirmed == null) { throw new RuntimeException("No order found by id " + event.getOrderId()); }
            OrderShippedEvent orderToShipEvent = new OrderShippedEvent(orderConfirmed.getOrderId());

            orderEventHandler.on(orderToShipEvent);

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }
}
