package com.nbloi.cqrses.query.service.kafkaproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.config.SerializerUtils;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@Slf4j
public class OrderCreatedEventProducer {

    private static final String TOPIC = "order_created_events";
    private final static long sendTimeout = 3000;

    Logger log = Logger.getLogger(OrderCreatedEventProducer.class.getSimpleName());
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OrderCreatedEventProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderEvent(@Payload String orderEvent) {
        try {
            kafkaTemplate.send(TOPIC, orderEvent).get(sendTimeout, TimeUnit.MILLISECONDS);
            LOGGER.info("Sent order event to kafka topic: " + TOPIC + " with record value: {}", orderEvent);
        } catch (Exception e) {
            LOGGER.info("KafkaEventBus publish get timeout", e);
            throw new RuntimeException(e);
        }
    }
}