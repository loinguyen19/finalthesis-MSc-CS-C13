package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentCreatedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OutboxMessage;
import com.nbloi.cqrses.query.entity.Payment;
import com.nbloi.cqrses.query.repository.CustomerRepository;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.PaymentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentEventHandler {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OutboxRepository outboxRepository;

    private final Logger logger = LoggerFactory.getLogger(PaymentEventHandler.class);

    @EventHandler
    public void onProcessing(PaymentCreatedEvent event) throws JsonProcessingException {
        // TODO: send message to outbox message. Then confirm payment is successful or failed.
        logger.info("Payment created in PaymentEventHandler: {}", event);
        Order order = orderRepository.findById(event.getOrderId()).get();
        Customer customer = customerRepository.findById(order.getCustomer().getCustomerId()).get();
        Payment payment = order.getPayment();
        BigDecimal remain = customer.getBalance().subtract(event.getTotalAmount());
        double remain2 = customer.getBalance().doubleValue() - event.getTotalAmount().doubleValue();
        if ( remain.doubleValue() >= 0) {
            customer.setBalance(BigDecimal.valueOf(remain2));
            customerRepository.save(customer);

            payment.setPaymentStatus(PaymentStatus.COMPLETED.toString());
            paymentRepository.save(payment);

            PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent();
            paymentCompletedEvent.setPaymentId(event.getPaymentId());
            paymentCompletedEvent.setOrderId(event.getOrderId());
            paymentCompletedEvent.setTotalAmount(event.getTotalAmount());
            paymentCompletedEvent.setPaymentStatus(payment.getPaymentStatus());
            paymentCompletedEvent.setCurrency(event.getCurrency());

            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(UUID.randomUUID().toString(),
                    paymentCompletedEvent.getPaymentId(),
                    EventType.PAYMENT_COMPLETED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(paymentCompletedEvent),
                    OutboxStatus.PENDING.toString());

            outboxRepository.save(outboxMessage);
        } else if (remain.doubleValue() < 0) {
            payment.setPaymentStatus(PaymentStatus.FAILED.toString());
            paymentRepository.save(payment);

            PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent();
            paymentFailedEvent.setPaymentId(event.getPaymentId());
            paymentFailedEvent.setOrderId(event.getOrderId());
            paymentFailedEvent.setTotalAmount(event.getTotalAmount());
            paymentFailedEvent.setPaymentStatus(payment.getPaymentStatus());
            paymentFailedEvent.setCurrency(event.getCurrency());

            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(UUID.randomUUID().toString(),
                    paymentFailedEvent.getPaymentId(),
                    EventType.PAYMENT_FAILED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(paymentFailedEvent),
                    OutboxStatus.PENDING.toString());

            outboxRepository.save(outboxMessage);
        } else {
            throw new RuntimeException("There some unknown error occurred");
        }
    }
}
