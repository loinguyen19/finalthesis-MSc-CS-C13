package com.nbloi.cqrses.message;

import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.query.entity.OutboxMessage;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.service.kafkaproducer.OrderCreatedEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxProcessor {

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OrderCreatedEventProducer orderCreatedEventProducer;

    @Transactional
    @Scheduled(fixedRate = 5000)  // Poll every 5 seconds
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxRepository.findPendingMessages();
        for (OutboxMessage message : messages) {
            try {
                // Publish message to Kafka
                orderCreatedEventProducer.sendOrderEvent(message.getPayload(), message.getAggregateId());

                // Mark message as PROCESSED
                message.setStatus(OutboxStatus.PROCESSED);
                message.setUpdatedAt(LocalDateTime.now());
                outboxRepository.save(message);
            } catch (Exception e) {
                // Handle failures (retry mechanism)
                handleFailure(message, e);
            }
        }
    }

    private void handleFailure(OutboxMessage message, Exception e) {
        // Increment retry count or mark as FAILED after max retries
        message.incrementRetryCount();
        if (message.getRetryCount() >= 5) {  // Configurable max retries
            message.setStatus(OutboxStatus.FAILED);
        }
        message.setUpdatedAt(LocalDateTime.now());
        outboxRepository.save(message);
    }
}