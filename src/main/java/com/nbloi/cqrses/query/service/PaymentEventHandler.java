package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Payment;
import com.nbloi.cqrses.query.repository.CustomerRepository;
import com.nbloi.cqrses.query.repository.PaymentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventHandler {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @EventHandler
    public void onProcessing(Payment payment) {
        Payment foundPayment = paymentRepository.findPaymentByCustomerId(payment.getOrder().getOrderId());

        if (foundPayment != null) {
            return;
        } else if (foundPayment.getTotalAmount().compareTo(payment.getTotalAmount()) < 0) {
            throw new RuntimeException("Your total balance is not sufficient to pay this payment");
        } else {

        }
    }
}
