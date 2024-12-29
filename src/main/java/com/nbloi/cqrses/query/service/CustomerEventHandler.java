package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.event.CustomerCreatedEvent;
import com.nbloi.cqrses.commonapi.query.FindAllCustomersQuery;
import com.nbloi.cqrses.commonapi.query.FindAllOrdersQuery;
import com.nbloi.cqrses.commonapi.query.FindCustomerByIdQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@Slf4j
public class CustomerEventHandler {

    @Autowired
    private CustomerRepository customerRepository;

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

    @QueryHandler
    public List<Customer> handle(FindAllCustomersQuery query) {
        return customerRepository.findAll();
    }

    @QueryHandler
    public Customer handle(FindCustomerByIdQuery query) {
        return customerRepository.findById(query.getCustomerId()).get();
    }
}
