package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.event.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerCreatedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerDeletedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerUpdatedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindAllCustomersQuery;
import com.nbloi.cqrses.commonapi.query.FindCustomerByIdQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OutboxMessage;
import com.nbloi.cqrses.query.repository.CustomerRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
@Slf4j
public class CustomerEventHandler {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private OutboxRepository outboxRepository;

    @EventHandler
    public void on(CustomerCreatedEvent event) throws JsonProcessingException {
        Customer customer = new Customer(
                event.getCustomerId(),
                event.getName(),
                event.getEmail(),
                event.getPhoneNumber(),
                event.getBalance(),
                event.getCreatedAt(),
                LocalDateTime.now());
        customerRepository.save(customer);

        // Save Outbox Message
        OutboxMessage outboxMessage = new OutboxMessage(
                UUID.randomUUID().toString(),
                event.getCustomerId(),
                EventType.CUSTOMER_CREATED_EVENT.toString(),
                new ObjectMapper().writeValueAsString(event),
                OutboxStatus.PENDING.toString()
        );
        outboxRepository.save(outboxMessage);
        log.info("Processing 'customer created event' OutboxMessage with payload: {}", outboxMessage.getPayload());
    }

    @EventHandler
    public void on(CustomerUpdatedEvent event) throws JsonProcessingException {
        String customerId = event.getCustomerId();
        Customer existingCustomer = handle(new FindCustomerByIdQuery(event.getCustomerId()));
        if (existingCustomer == null) {throw new UnfoundEntityException(customerId, Customer.class.getSimpleName());}

        existingCustomer.setName(event.getName());
        existingCustomer.setEmail(event.getEmail());
        existingCustomer.setPhoneNumber(event.getPhoneNumber());
        existingCustomer.setBalance(event.getBalance());
        existingCustomer.setUpdatedAt(LocalDateTime.now());

        // Save the updated customer
        customerRepository.save(existingCustomer);

        // Save Outbox Message
        OutboxMessage outboxMessage = new OutboxMessage(
                UUID.randomUUID().toString(),
                event.getCustomerId(),
                EventType.CUSTOMER_UPDATED_EVENT.toString(),
                new ObjectMapper().writeValueAsString(event),
                OutboxStatus.PENDING.toString()
        );
        outboxRepository.save(outboxMessage);
        log.info("Processing 'customer updated event' OutboxMessage with payload: {}", outboxMessage.getPayload());
    }

    @EventHandler
    public void delete(CustomerDeletedEvent event) throws JsonProcessingException {
        String customerId = event.getCustomerId();
        try {
            Customer existingCustomer = handle(new FindCustomerByIdQuery(event.getCustomerId()));
            customerRepository.delete(existingCustomer);
        } catch (Exception e) {
            throw new UnfoundEntityException(customerId, Customer.class.getSimpleName());
        }

        // Save Outbox Message
        OutboxMessage outboxMessage = new OutboxMessage(
                UUID.randomUUID().toString(),
                event.getCustomerId(),
                EventType.CUSTOMER_DELETED_EVENT.toString(),
                new ObjectMapper().writeValueAsString(event),
                OutboxStatus.PENDING.toString()
        );
        outboxRepository.save(outboxMessage);
        log.info("Processing 'customer deleted event' OutboxMessage with payload: {}", outboxMessage.getPayload());
    }

    @EventHandler
    public void revertBalance(PaymentFailedEvent event) {
        Order order = orderEventHandler.handle(new FindOrderByIdQuery(event.getOrderId()));
        Customer customer = handle(new FindCustomerByIdQuery(order.getCustomer().getCustomerId()));
        customer.setBalance(customer.getBalance().add(event.getTotalAmount()));
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @QueryHandler
    public List<Customer> handle(FindAllCustomersQuery query) {
        List<Customer> customerList = customerRepository.findAll();
            if (!customerList.isEmpty()) {
                return customerList;
            } else {
                return new ArrayList<>();
            }
    }

    @QueryHandler
    public Customer handle(FindCustomerByIdQuery query) {
        Customer customer = customerRepository.findById(query.getCustomerId()).orElse(null);
        if (customer == null) {throw new UnfoundEntityException(query.getCustomerId(), Customer.class.getSimpleName());}
        return customer;
    }
}
