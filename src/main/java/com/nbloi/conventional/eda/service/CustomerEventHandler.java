    package com.nbloi.conventional.eda.service;

import com.nbloi.conventional.eda.event.customer.CustomerCreatedEvent;
import com.nbloi.conventional.eda.event.customer.CustomerDeletedEvent;
import com.nbloi.conventional.eda.event.customer.CustomerUpdatedEvent;
import com.nbloi.conventional.eda.exception.UnfoundEntityException;
import com.nbloi.conventional.eda.entity.Customer;
import com.nbloi.conventional.eda.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void on(CustomerCreatedEvent event) {
        Customer customer = new Customer(
                event.getCustomerId(),
                event.getName(),
                event.getEmail(),
                event.getPhoneNumber(),
                event.getBalance());
        customerRepository.save(customer);
    }

    public void on(CustomerUpdatedEvent event) {
        String customerId = event.getCustomerId();
        Customer existingCustomer = readCustomerById(event.getCustomerId());
        if (existingCustomer == null) {throw new UnfoundEntityException(customerId, Customer.class.getSimpleName());}

        existingCustomer.setName(event.getName());
        existingCustomer.setEmail(event.getEmail());
        existingCustomer.setPhoneNumber(event.getPhoneNumber());
        existingCustomer.setBalance(event.getBalance());

        // Save the updated customer
        customerRepository.save(existingCustomer);
    }

    public void delete(CustomerDeletedEvent event) {
        String customerId = event.getCustomerId();
        try {
            Customer existingCustomer = readCustomerById(event.getCustomerId());
            customerRepository.delete(existingCustomer);
        } catch (Exception e) {
            throw new UnfoundEntityException(customerId, Customer.class.getSimpleName());
        }
    }

    public List<Customer> readAllCustomers() {
        List<Customer> customerList = customerRepository.findAll();
            if (!customerList.isEmpty()) {
                return customerList;
            } else {
                return new ArrayList<>();
            }
    }

    public Customer readCustomerById(String customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {throw new UnfoundEntityException(customerId, Customer.class.getSimpleName());}
        return customer;
    }
}
