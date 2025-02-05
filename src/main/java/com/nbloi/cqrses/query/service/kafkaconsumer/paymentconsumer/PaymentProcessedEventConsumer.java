package com.nbloi.cqrses.query.service.kafkaconsumer.paymentconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.payment.PaymentCreatedEvent;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import com.nbloi.cqrses.query.service.PaymentEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentProcessedEventConsumer {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventHandler orderEventHandler;

    @Autowired
    private PaymentEventHandler paymentEventHandler;

    @KafkaListener(topics = "payment_created_events", groupId = "payment_group")
    public void handlePaymentEvent(@Payload String paymentCreatedEvent) {
        // Process the payment event, e.g., update payment status
        log.info("Received Payment Event: " + paymentCreatedEvent);
        // Implement the logic for payment processing and order status update

        try {
            // TODO: call onProcessing in PaymentEventHandler to update the balance of customer and call function go to confirm payment

            PaymentCreatedEvent paymentEvent = new ObjectMapper().readValue(paymentCreatedEvent, PaymentCreatedEvent.class);
            paymentEventHandler.onProcessing(paymentEvent);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
