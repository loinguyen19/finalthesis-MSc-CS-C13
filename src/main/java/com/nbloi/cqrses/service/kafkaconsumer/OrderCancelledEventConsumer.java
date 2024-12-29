package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.OrderCancelledEvent;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderCancelledEventConsumer {

    @Autowired
    private OrderEventHandler orderEventHandler;

    @Autowired
    private OutboxRepository outboxRepository;

    @KafkaListener(topics = "payment_failed_events", groupId = "order_group")
    public void handleOrderCancelledEvent(@Payload String paymentFailedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Failed Event: " + paymentFailedEvent);

        // Implement the logic for order confirmation processing
        try{
            PaymentFailedEvent paymentEvent = new ObjectMapper().readValue(paymentFailedEvent, PaymentFailedEvent.class);

//            OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(paymentEvent.getOrderId());
//            Order orderCancelled = orderEventHandler.handle(new FindOrderByIdQuery(paymentEvent.getOrderId()));
//
//            if (orderCancelled == null) { throw new RuntimeException("No order found by id " + paymentEvent.getOrderId()); }
            OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(paymentEvent.getOrderId());

            orderEventHandler.on(orderCancelledEvent);

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }
}
