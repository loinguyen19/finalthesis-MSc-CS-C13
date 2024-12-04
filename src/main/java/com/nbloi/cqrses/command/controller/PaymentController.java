package com.nbloi.cqrses.command.controller;

import com.nbloi.cqrses.commonapi.event.PaymentEvent;
import com.nbloi.cqrses.query.service.kafkaproducer.PaymentEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @PostMapping("/payment")
    public ResponseEntity<String> payment(@RequestBody PaymentEvent paymentEvent) {
        if (paymentEvent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        paymentEventProducer.sendPaymentEvent(paymentEvent);
        return ResponseEntity.ok("Payment processed successfully");
    }
}
