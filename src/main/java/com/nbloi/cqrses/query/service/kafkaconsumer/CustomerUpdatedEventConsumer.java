package com.nbloi.cqrses.query.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.payment.PaymentCompletedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerUpdatedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.service.CustomerEventHandler;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class CustomerUpdatedEventConsumer {

    @Autowired
    private CustomerEventHandler customerEventHandler;

    @Autowired
    private OrderEventHandler orderEventHandler;

    @Autowired
    private OutboxRepository outboxRepository;

    @KafkaListener(topics = "payment_completed_events", groupId = "customer_group")
    public void handleCustomerUpdatedEvent(@Payload String paymentCompletedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Customer Updated Event: " + paymentCompletedEvent);

        // Implement the logic for order confirmation processing
        try{
            PaymentCompletedEvent paymentEvent = new ObjectMapper().readValue(paymentCompletedEvent, PaymentCompletedEvent.class);

            Order order = orderEventHandler.handle(new FindOrderByIdQuery(paymentEvent.getOrderId()));
            System.out.println("Retrieved Order from Payment Event: " + order.toString());

            if (order == null) { throw new UnfoundEntityException(paymentEvent.getOrderId(), Order.class.toString());}

            Customer customer = order.getCustomer();
            CustomerUpdatedEvent customerUpdatedEvent = new CustomerUpdatedEvent(
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    customer.getBalance().subtract(paymentEvent.getTotalAmount())
            );
            System.out.println("Update-to-be Customer from Order Retrieved: " + customerUpdatedEvent.toString());

            customerEventHandler.on(customerUpdatedEvent);

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleCustomerUpdatedEvent");
        }
    }
}
