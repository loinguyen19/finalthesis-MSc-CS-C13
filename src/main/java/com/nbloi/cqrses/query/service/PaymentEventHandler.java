package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.payment.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentCreatedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Transactional
@Service
public class PaymentEventHandler {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderEventHandler orderEventHandler;

    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public PaymentEventHandler(PaymentRepository paymentRepository, OrderEventHandler orderEventHandler, OutboxRepository outboxRepository) {
        super();
        this.paymentRepository = paymentRepository;
        this.orderEventHandler = orderEventHandler;
        this.outboxRepository = outboxRepository;
    }

    private final Logger logger = LoggerFactory.getLogger(PaymentEventHandler.class);

    @EventHandler
    public void onProcessing(PaymentCreatedEvent event) throws JsonProcessingException {
        // TODO: send message to outbox message. Then confirm payment is successful or failed.
        logger.info("Payment created in PaymentEventHandler: {}", event);
        Order order = orderEventHandler.handle(new FindOrderByIdQuery(event.getOrderId()));

        String customerId = order.getCustomer().getCustomerId();
        Customer customer = order.getCustomer();
        if (customer == null) {throw new UnfoundEntityException(customerId, Customer.class.getSimpleName());}

        Payment payment = order.getPayment();
        BigDecimal remain = customer.getBalance().subtract(event.getTotalAmount());
        if ( remain.doubleValue() >= 0) {

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
