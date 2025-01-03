package com.nbloi.cqrses.query.service.kafkaconsumer.paymentconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.order.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.Payment;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.PaymentRepository;
import com.nbloi.cqrses.query.service.PaymentSummaryProjectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentSummarizedEventConsumer {

    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentSummaryProjectionHandler paymentSummaryProjectionHandler;

    @KafkaListener(topics = "order_shipped_events", groupId = "payment_group")
    public void handlePaymentSummarized(@Payload String orderShippedEvent) throws JsonProcessingException {
        // Process the payment event, e.g., update payment status
        log.info("Received Order Shipped Event: {} to send to Payment Summarized View", orderShippedEvent);
        OrderShippedEvent shippedEvent = new ObjectMapper().readValue(orderShippedEvent, OrderShippedEvent.class);
        Order order = orderRepository.findById(shippedEvent.getOrderId()).orElseThrow(()
                -> new UnfoundEntityException(shippedEvent.getOrderId(),OrderShippedEvent.class.getName()));
        Payment payment = order.getPayment();
        String paymentStatus = payment.getPaymentStatus();

        if (paymentStatus.contains(PaymentStatus.COMPLETED.toString())) {
            // Implement the logic to handle the event based on its type or content
            log.info("Handling payment completed event");
            try {
                PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent();
                paymentCompletedEvent.setPaymentId(payment.getPaymentId());
                paymentCompletedEvent.setOrderId(shippedEvent.getOrderId());
                paymentCompletedEvent.setPaymentStatus(paymentStatus);
                paymentCompletedEvent.setPaymentMethods(payment.getPaymentMethods());
                paymentCompletedEvent.setPaymentDate(payment.getPaymentDate());
                paymentCompletedEvent.setTotalAmount(payment.getTotalAmount());
                paymentCompletedEvent.setCurrency(payment.getCurrency());

                paymentSummaryProjectionHandler.onPaymentSummarize(paymentCompletedEvent);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else if (paymentStatus.contains(PaymentStatus.FAILED.toString())) {
            log.info("Handling payment failed event");
            try {
                PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent();
                paymentFailedEvent.setPaymentId(payment.getPaymentId());
                paymentFailedEvent.setOrderId(shippedEvent.getOrderId());
                paymentFailedEvent.setPaymentStatus(paymentStatus);
                paymentFailedEvent.setPaymentMethods(payment.getPaymentMethods());
                paymentFailedEvent.setPaymentDate(payment.getPaymentDate());
                paymentFailedEvent.setTotalAmount(payment.getTotalAmount());
                paymentFailedEvent.setCurrency(payment.getCurrency());

                paymentSummaryProjectionHandler.onPaymentSummarize(paymentFailedEvent);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

//    @KafkaListener(topics = "payment_failed_events", groupId = "payment_group")
//    public void handlePaymentSummarizedForFailedEvent(@Payload String paymentFailedEvent) {
//        // Process the payment event, e.g., update payment status
//        log.info("Received Payment Completed Event: {} to send to Payment Summarized View", paymentFailedEvent);
//            // TODO: call onProcessing in PaymentEventHandler to update the balance of customer and call function go to confirm payment            // TODO: send message to outbox message. Then confirm payment is successful or failed.
//
//        log.info("Handling payment failed event");
//        // Your logic here
//        try {
//            PaymentFailedEvent paymentEvent = new ObjectMapper().readValue(paymentFailedEvent, PaymentFailedEvent.class);
//            paymentSummaryProjectionHandler.onPaymentSummarize(paymentEvent);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
}
