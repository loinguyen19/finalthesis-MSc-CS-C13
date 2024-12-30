package com.nbloi.conventional.eda.repository;

import com.nbloi.conventional.eda.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

//    Payment findPaymentByCustomerId(@Param("customerId") String customerId);

//    Payment findPaymentByOrderId(@Param("orderId") String orderId);
}
