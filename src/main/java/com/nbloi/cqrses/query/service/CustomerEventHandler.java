package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.event.customer.CustomerCreatedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerDeletedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerUpdatedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindAllCustomersQuery;
import com.nbloi.cqrses.commonapi.query.FindCustomerByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@Slf4j
public class CustomerEventHandler {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private View error;

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        Customer customer = new Customer(
                event.getCustomerId(),
                event.getName(),
                event.getEmail(),
                event.getPhoneNumber(),
                event.getBalance(),
                event.getCreatedAt());
        customerRepository.save(customer);
    }

    @EventHandler
    public void on(CustomerUpdatedEvent event) {
        String customerId = event.getCustomerId();
        Customer existingCustomer = handle(new FindCustomerByIdQuery(event.getCustomerId()));
        if (existingCustomer == null) {throw new UnfoundEntityException(customerId, Customer.class.getSimpleName());}

        existingCustomer.setName(event.getName());
        existingCustomer.setEmail(event.getEmail());
        existingCustomer.setPhoneNumber(event.getPhoneNumber());
        existingCustomer.setBalance(event.getBalance());

        // Save the updated customer
        customerRepository.save(existingCustomer);
    }

    @EventHandler
    public void delete(CustomerDeletedEvent event) {
        String customerId = event.getCustomerId();
        try {
            Customer existingCustomer = handle(new FindCustomerByIdQuery(event.getCustomerId()));
            customerRepository.delete(existingCustomer);
        } catch (Exception e) {
            throw new UnfoundEntityException(customerId, Customer.class.getSimpleName());
        }
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
