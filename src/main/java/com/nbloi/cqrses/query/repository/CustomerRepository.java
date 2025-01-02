package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.commonapi.enums.CustomerStatus;
import com.nbloi.cqrses.query.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    public List<Customer> findAllByCustomerStatus(String customerStatus);
    public Customer findByCustomerIdAndCustomerStatus(String customerId, String customerStatus);
    public Customer findByCustomerId(String customerId);
}
