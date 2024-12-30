package com.nbloi.conventional.eda.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.enums.PaymentStatus;
import com.nbloi.conventional.eda.event.PaymentCompletedEvent;
import com.nbloi.conventional.eda.event.PaymentCreatedEvent;
import com.nbloi.conventional.eda.event.PaymentFailedEvent;
import com.nbloi.conventional.eda.entity.Customer;
import com.nbloi.conventional.eda.entity.Order;
import com.nbloi.conventional.eda.entity.Payment;
import com.nbloi.conventional.eda.repository.CustomerRepository;
import com.nbloi.conventional.eda.repository.OrderRepository;
import com.nbloi.conventional.eda.repository.PaymentRepository;
import com.nbloi.conventional.eda.service.kafkaproducer.PaymentEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentEventHandler {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PaymentEventProducer paymentEventProducer;

    private final Logger log = LoggerFactory.getLogger(PaymentEventHandler.class);


    public void onProcessing(PaymentCreatedEvent event) throws JsonProcessingException {
        // TODO: send message to outbox message. Then confirm payment is successful or failed.
        log.info("Payment created in PaymentEventHandler: {}", event);
        Order order = orderRepository.findById(event.getOrderId()).get();
        Customer customer = customerRepository.findById(order.getCustomer().getCustomerId()).get();
        Payment payment = order.getPayment();
        BigDecimal remain = customer.getBalance().subtract(event.getTotalAmount());
        if ( remain.doubleValue() >= 0) {
            customer.setBalance(customer.getBalance().subtract(event.getTotalAmount()));
            customerRepository.save(customer);

            payment.setPaymentStatus(PaymentStatus.COMPLETED.toString());
            paymentRepository.save(payment);

            PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent();
            paymentCompletedEvent.setPaymentId(event.getPaymentId());
            paymentCompletedEvent.setOrderId(event.getOrderId());
            paymentCompletedEvent.setTotalAmount(event.getTotalAmount());
            paymentCompletedEvent.setPaymentStatus(payment.getPaymentStatus());
            paymentCompletedEvent.setCurrency(event.getCurrency());

            String paymentCompletedEventPayload = new ObjectMapper().writeValueAsString(paymentCompletedEvent);

            // Send PaymentCompletedEvent to Kafka broker
            paymentEventProducer.sendPaymentCompletedEvent(paymentCompletedEventPayload);
            log.info("Payment completed in PaymentEventHandler: {}", paymentCompletedEventPayload);

        } else if (remain.doubleValue() < 0) {
            payment.setPaymentStatus(PaymentStatus.FAILED.toString());
            paymentRepository.save(payment);

            PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent();
            paymentFailedEvent.setPaymentId(event.getPaymentId());
            paymentFailedEvent.setOrderId(event.getOrderId());
            paymentFailedEvent.setTotalAmount(event.getTotalAmount());
            paymentFailedEvent.setPaymentStatus(payment.getPaymentStatus());
            paymentFailedEvent.setCurrency(event.getCurrency());

            String paymentFailedEventPayload = new ObjectMapper().writeValueAsString(paymentFailedEvent);

            // Send PaymentFailedEventPayload to Kafka broker
            paymentEventProducer.sendPaymentFailedEvent(paymentFailedEventPayload);
            log.info("Payment failed in PaymentEventHandler: {}", paymentFailedEventPayload);

        } else {
            throw new RuntimeException("There some unknown error occurred");
        }
    }

    public List<Order> readAllPayments() {
        return orderRepository.findAll();
    }

    public Order readPaymentById(String orderId) {
        return orderRepository.findById(orderId).get();
    }
}