package com.nbloi.cqrses.query.service.kafkaproducer;

import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatedEventProducer {

    private static final String TOPIC = "order_created_events";

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderEvent(OrderCreatedEvent orderEvent) {
        kafkaTemplate.send(TOPIC, orderEvent);
    }
}