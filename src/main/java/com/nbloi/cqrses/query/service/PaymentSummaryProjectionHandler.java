package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.event.PaymentFailedEvent;
import com.nbloi.cqrses.query.entity.PaymentSummaryView;
import com.nbloi.cqrses.query.repository.PaymentSummaryViewRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PaymentSummaryProjectionHandler {

    @Autowired
    private PaymentSummaryViewRepository paymentSummaryViewRepository;

    @EventHandler
    public void on(PaymentCompletedEvent event) {

        PaymentSummaryView view = new PaymentSummaryView();
        view.setOrderId(event.getOrderId());
        view.setPaymentId(event.getPaymentCompletedId());
        view.setPaymentStatus("COMPLETED");
        view.setPaymentDate(event.getPaymentDate());
        view.setPaymentTotalAmount(event.getTotalAmount());
        paymentSummaryViewRepository.save(view);

    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        PaymentSummaryView view = paymentSummaryViewRepository.findById(event.getPaymentFailedId()).get();
        view.setPaymentStatus(PaymentStatus.FAILED.toString());
        paymentSummaryViewRepository.save(view);
    }
}
