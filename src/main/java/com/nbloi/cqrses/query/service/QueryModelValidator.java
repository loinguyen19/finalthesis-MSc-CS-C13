package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.query.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class QueryModelValidator {
    private final CustomerRepository customerRepository;

    public QueryModelValidator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public boolean aggregateExists(String aggregateId) {
        return customerRepository.existsById(aggregateId);
    }
}