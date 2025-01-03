package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.commonapi.enums.CustomerStatus;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    public Customer findByCustomerId(String customerId);

    @Query("SELECT p FROM Customer p WHERE p.customerStatus = 'ACTIVE' ")
    public List<Customer> findAllActiveCustomer();

    @Query("SELECT p FROM Customer p WHERE p.customerStatus = 'ACTIVE' AND p.customerId= :customerId ")
    public Customer findActiveCustomerById(@Param("customerId") String customerId);
}
