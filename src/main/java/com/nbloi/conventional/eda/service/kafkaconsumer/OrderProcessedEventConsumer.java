package com.nbloi.conventional.eda.service.kafkaconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.entity.Customer;
import com.nbloi.conventional.eda.entity.Order;
import com.nbloi.conventional.eda.event.OrderCancelledEvent;
import com.nbloi.conventional.eda.event.OrderDeletedEvent;
import com.nbloi.conventional.eda.event.PaymentFailedEvent;
import com.nbloi.conventional.eda.event.customer.CustomerDeletedEvent;
import com.nbloi.conventional.eda.service.CustomerEventHandler;
import com.nbloi.conventional.eda.service.OrderEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderProcessedEventConsumer {

    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private CustomerEventHandler customerEventHandler;

    private final Map<String, Integer> processedEventsMap = new HashMap<String, Integer>();

    @KafkaListener(topics = "payment_failed_events", groupId = "order_group")
    public void handleOrderCancelledEvent(@Payload String paymentFailedEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Failed Event: " + paymentFailedEvent);

        // Implement the logic for order confirmation processing
        try{
            PaymentFailedEvent paymentEvent = new ObjectMapper().readValue(paymentFailedEvent, PaymentFailedEvent.class);
            OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(paymentEvent.getOrderId());
            orderEventHandler.on(orderCancelledEvent);

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }

    @KafkaListener(topics = "customer_deleted_events", groupId = "order_group")
    public void handleOrderDeletedEventWhenDeletingACustomer(@Payload String customerDeletedEvent) {
        System.out.println("Received Order Deleted Event When Deleting A Customer: " + customerDeletedEvent);

        try {
            CustomerDeletedEvent customerEvent = new ObjectMapper().readValue(customerDeletedEvent, CustomerDeletedEvent.class);
            Customer customer = customerEventHandler.readCustomerById(customerEvent.getCustomerId());
            String customerDeletedEventId = customerEvent.getCustomerDeletedEventId();

            int i=0;
            // check if the event processed or not
                for (Order order : customer.getOrders()) {
                    if (order != null) {
                        OrderDeletedEvent orderDeletedEvent = new OrderDeletedEvent(order.getOrderId());
                        // Process the event
                        orderEventHandler.on(orderDeletedEvent);
                    }
                    // Mark the event as processed status
                    processedEventsMap.put(customerDeletedEventId, i++);
                }

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }

    public int getProcessedCount(String eventId) {
        return processedEventsMap.getOrDefault(eventId, 0);
    }
}
