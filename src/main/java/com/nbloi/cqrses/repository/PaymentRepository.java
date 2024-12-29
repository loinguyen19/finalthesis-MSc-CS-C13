package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

//    Payment findPaymentByCustomerId(@Param("customerId") String customerId);

//    Payment findPaymentByOrderId(@Param("orderId") String orderId);
}
