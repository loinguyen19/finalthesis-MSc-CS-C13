package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentEvent;
import com.nbloi.cqrses.query.entity.OrderDetails;
import com.nbloi.cqrses.query.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConfirmedEventConsumer {

    @Autowired
    OrderRepository orderRepository;

    @KafkaListener(topics = "payment_events", groupId = "payment_group")
    public void handleOrderConfirmedEvent(@Payload PaymentEvent paymentEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Event: " + paymentEvent);

        // Implement the logic for order confirmation processing
        try{
            OrderDetails orderToConfirm = orderRepository.findById(paymentEvent.getOrderItemId()).orElse(null);

            if (orderToConfirm == null) { throw new RuntimeException("No order found by id " + paymentEvent.getOrderItemId()); }
            orderToConfirm.setOrderConfirmed();

            orderRepository.save(orderToConfirm);

        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }
}
