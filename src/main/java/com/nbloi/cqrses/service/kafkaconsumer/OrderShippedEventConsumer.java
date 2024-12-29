package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OutboxMessage;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderShippedEventConsumer {

    @Autowired
    private OrderEventHandler orderEventHandler;

    @Autowired
    private OutboxRepository outboxRepository;

    @KafkaListener(topics = "order_confirmed_events", groupId = "order_group")
    public void handleOrderShippedEvent(@Payload String orderConfirmedEvent) {
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
