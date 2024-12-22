package com.nbloi.cqrses.command.controller;

import com.nbloi.cqrses.commonapi.command.PaymentCommand;
import com.nbloi.cqrses.commonapi.event.PaymentCompletedEvent;
import com.nbloi.cqrses.query.service.kafkaproducer.PaymentEventProducer;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @PostMapping("/payment")
    public ResponseEntity<String> payment(@RequestBody PaymentCompletedEvent paymentCompletedEvent) {
        if (paymentCompletedEvent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        commandGateway.send(new PaymentCommand(paymentCompletedEvent.getPaymentId(), paymentCompletedEvent.getAmount(),
                paymentCompletedEvent.getCurrency(), paymentCompletedEvent.getOrderId()));

        // send the event to Kafka broker in PaymentEventHandler service class
        // paymentEventProducer.sendPaymentEvent(paymentEvent);
        return ResponseEntity.ok("Payment processed successfully");
    }
}
