package com.nbloi.conventional.eda.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.event.customer.CustomerUpdatedEvent;
import com.nbloi.conventional.eda.event.PaymentCompletedEvent;
import com.nbloi.conventional.eda.exception.UnfoundEntityException;
import com.nbloi.conventional.eda.entity.Customer;
import com.nbloi.conventional.eda.entity.Order;
import com.nbloi.conventional.eda.service.CustomerEventHandler;
import com.nbloi.conventional.eda.service.OrderEventHandler;
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


    @KafkaListener(topics = "payment_completed_events", groupId = "customer_group")
    public void handleCustomerUpdatedEvent(@Payload String paymentCompletedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Customer Updated Event: " + paymentCompletedEvent);

        // Implement the logic for order confirmation processing
        try{
            PaymentCompletedEvent paymentEvent = new ObjectMapper().readValue(paymentCompletedEvent, PaymentCompletedEvent.class);

            Order order = orderEventHandler.readOrderById(paymentEvent.getOrderId());
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
