package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.CustomerOrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrderView, String> {

    public CustomerOrderView findByCustomerId(String customerId);
    public CustomerOrderView findByOrderId(String orderId);
    public CustomerOrderView findByCustomerIdAndOrderId(String customerId, String orderId);
    public List<CustomerOrderView> findAllByCustomerName(String customerName);
}
