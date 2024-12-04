package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.command.controller.OrderController;
import com.nbloi.cqrses.commonapi.dto.ConfirmOrderRequestDTO;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
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
public class PaymentEventConsumer {

    @Autowired
    OrderRepository orderRepository;

    @KafkaListener(topics = "payment_events", groupId = "payment_group")
    public void handlePaymentEvent(@Payload PaymentEvent paymentEvent) {
        // Process the payment event, e.g., update payment status
        System.out.println("Received Payment Event: " + paymentEvent);
        // Implement the logic for payment processing and order status update

        OrderConfirmedEvent orderConfirmedEvent;
        try {
            orderConfirmedEvent = new ObjectMapper().convertValue(paymentEvent, OrderConfirmedEvent.class);
            System.out.println(orderConfirmedEvent);

            if (orderConfirmedEvent.getOrderItemId() != null) {
                OrderDetails orderDetailsToSave = new ObjectMapper().convertValue(orderConfirmedEvent, OrderDetails.class) ;
                orderRepository.save(orderDetailsToSave);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
