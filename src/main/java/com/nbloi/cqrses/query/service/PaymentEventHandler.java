package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @EventHandler
    public void onProcessing(PaymentCreatedEvent event) throws JsonProcessingException {
        Order order = orderRepository.findById(event.getOrderId()).get();
        Customer customer = customerRepository.findById(order.getCustomer().getCustomerId()).get();

        if (customer.getBalance().compareTo(event.getTotalAmount()) >= 0) {
            customer.setBalance(customer.getBalance().subtract(event.getTotalAmount()));
            customerRepository.save(customer);

            PaymentCompletedEvent paymentCompletedEvent = new ObjectMapper().convertValue(event, PaymentCompletedEvent.class);

            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(UUID.randomUUID().toString(),
                    paymentCompletedEvent.getPaymentId(),
                    EventType.PAYMENT_COMPLETED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(paymentCompletedEvent),
                    OutboxStatus.PENDING.toString());

            outboxRepository.save(outboxMessage);
        }
        else if (customer.getBalance().compareTo(event.getTotalAmount()) < 0) {
            PaymentFailedEvent paymentFailedEvent = new ObjectMapper().convertValue(event, PaymentFailedEvent.class);
            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(UUID.randomUUID().toString(),
                    paymentFailedEvent.getPaymentId(),
                    EventType.PAYMENT_FAILED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(paymentFailedEvent),
                    OutboxStatus.PENDING.toString());

            outboxRepository.save(outboxMessage);
        }
        else {
            throw new RuntimeException("There some unknown error occurred");
        }
    }
}
