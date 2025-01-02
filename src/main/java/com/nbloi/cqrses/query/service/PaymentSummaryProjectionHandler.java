package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.event.payment.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentFailedEvent;
import com.nbloi.cqrses.query.entity.OutboxMessage;
import com.nbloi.cqrses.query.entity.PaymentSummaryView;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.PaymentRepository;
import com.nbloi.cqrses.query.repository.PaymentSummaryViewRepository;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentSummaryProjectionHandler {

    @Autowired
    private PaymentSummaryViewRepository paymentSummaryViewRepository;
    @Autowired
    private OutboxRepository outboxRepository;
    private final Logger log = LoggerFactory.getLogger(PaymentSummaryProjectionHandler.class);

    public PaymentSummaryProjectionHandler(PaymentSummaryViewRepository paymentSummaryViewRepository, OutboxRepository outboxRepository) {
        super();
        this.paymentSummaryViewRepository = paymentSummaryViewRepository;
        this.outboxRepository = outboxRepository;
    }

    @EventHandler
    public void onPaymentSummarize(PaymentCompletedEvent event) throws JsonProcessingException {
        PaymentSummaryView view = generatePaymentSummaryView(event.getOrderId(), event.getPaymentId(),
                event.getPaymentStatus(), event.getPaymentDate(), event.getTotalAmount());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String paymentSummaryEventPayload = objectMapper.writeValueAsString(view);

        // Create message for outbox message
        OutboxMessage outboxMessage = new OutboxMessage(UUID.randomUUID().toString(),
                view.getPaymentSummaryId(),
                EventType.PAYMENT_SUMMARIZED_EVENT.toString(),
                new ObjectMapper().writeValueAsString(paymentSummaryEventPayload),
                OutboxStatus.PENDING.toString());

        // Send message to Outbox message queue for Product Inventory Event
        outboxRepository.save(outboxMessage);
        log.info("Processing message PaymentCompletedEvent in PaymentSummaryView in OutboxMessage with payload: {}",
                paymentSummaryEventPayload);
    }

    @EventHandler
    public void onPaymentSummarize(PaymentFailedEvent event) throws JsonProcessingException {
        PaymentSummaryView viewForPaymentFailedEvent = generatePaymentSummaryView(event.getOrderId(), event.getPaymentId(),
                event.getPaymentStatus(), event.getPaymentDate(), event.getTotalAmount());

//        view.setPaymentStatus(PaymentStatus.FAILED.toString());

        String paymentSummaryEventPayload = new ObjectMapper().writeValueAsString(viewForPaymentFailedEvent);

        // Create message for outbox message
        OutboxMessage outboxMessage = new OutboxMessage(UUID.randomUUID().toString(),
                viewForPaymentFailedEvent.getPaymentSummaryId(),
                EventType.PAYMENT_SUMMARIZED_EVENT.toString(),
                new ObjectMapper().writeValueAsString(paymentSummaryEventPayload),
                OutboxStatus.PENDING.toString());

        // Send message to Outbox message queue for Product Inventory Event
        outboxRepository.save(outboxMessage);
        log.info("Processing message PaymentFailedEvent in PaymentSummaryView in OutboxMessage with payload: {}",
                outboxMessage.getPayload());
    }

    private PaymentSummaryView generatePaymentSummaryView(String orderId, String paymentId, String PaymentStatus,
                                            LocalDateTime paymentDate, BigDecimal totalAmount) {
        PaymentSummaryView paymentSummaryView = new PaymentSummaryView(
            UUID.randomUUID().toString(),
            orderId,
            paymentId,
            PaymentStatus,
            paymentDate,
            totalAmount);
        paymentSummaryViewRepository.save(paymentSummaryView);

        return paymentSummaryView;
    }
}
