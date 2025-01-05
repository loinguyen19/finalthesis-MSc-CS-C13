package com.nbloi.cqrses.query.service.kafkaconsumer.orderconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.event.order.OrderCancelledEvent;
import com.nbloi.cqrses.commonapi.event.order.OrderDeletedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerDeletedEvent;
import com.nbloi.cqrses.commonapi.event.product.ProductDeletedEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.customer.FindCustomerByIdQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdAllStatusQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.OrderItemRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.CustomerEventHandler;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import com.nbloi.cqrses.query.service.ProductEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderProcessedEventConsumer {

    @Autowired
    private CustomerEventHandler customerEventHandler;
    @Autowired
    private OrderEventHandler orderEventHandler;

    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private ProductEventHandler productEventHandler;

    private final Map<String, Integer> processedEventsMap = new HashMap<String, Integer>();
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;

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
            Customer customer = customerEventHandler.handle(new FindCustomerByIdQuery(customerEvent.getCustomerId()));
            String customerDeletedEventId = customerEvent.getCustomerDeletedEventId();

            // check if the event processed or not
            if (!processedEventsMap.containsKey(customerDeletedEventId)) {
                for (Order order : customer.getOrders()) {
                    if (order != null) {
                        OrderDeletedEvent orderDeletedEvent = new OrderDeletedEvent(order.getOrderId());
                        // Process the event
                        orderEventHandler.on(orderDeletedEvent);
                    }
                }

                // Mark the event as processed status
                processedEventsMap.put(customerDeletedEventId, 1);
            }

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }

    @KafkaListener(topics = "product_deleted_events", groupId = "product_group")
    public void handleOrderDeletedEventWhenDeletingAProduct(@Payload String productDeletedEvent) {
        System.out.println("Received Order Deleted Event When Deleting A Customer: " + productDeletedEvent);

        try {
            ProductDeletedEvent productEvent = new ObjectMapper().readValue(productDeletedEvent, ProductDeletedEvent.class);
            Product product = productEventHandler.handle(new FindProductByIdAllStatusQuery(productEvent.getProductId()));
//            Product product = productRepository.findById(productEvent.getProductId()).orElse(null);
            if (product != null) {throw new UnfoundEntityException(productEvent.getProductId(), Product.class.getName());}
            String productDeletedEventId = productEvent.getProductDeletedEventId();

            // check if the event processed or not
            if (!processedEventsMap.containsKey(productDeletedEventId)) {
                List<OrderItem> orderItemList = orderItemRepository.findByProduct(product);

                for (OrderItem orderItem : orderItemList) {
                    Order order = orderItem.getOrder();
                    if (order != null) {
                        OrderDeletedEvent orderDeletedEvent = new OrderDeletedEvent(order.getOrderId());
                        // Process the event
                        orderEventHandler.on(orderDeletedEvent);
                    }
                }

                // Mark the event as processed status
                processedEventsMap.put(productDeletedEventId, 1);
            }

        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
        }
    }

    public int getProcessedCount(String eventId) {
        return processedEventsMap.getOrDefault(eventId, 0);
    }

//    @KafkaListener(topics = "product_updated_events", groupId = "order_group")
//    public void handleOrderDeletedEventWhenDeletingAProduct(@Payload String productDeletedEvent) {
//        System.out.println("Received Order Deleted Event When Deleting A Product: " + productDeletedEvent);
//
//        try {
//            ProductDeletedEvent productEvent = new ObjectMapper().readValue(productDeletedEvent, ProductDeletedEvent.class);
//            Product product = productEventHandler.handle(new FindCustomerByIdQuery(productEvent.getProductId()));
//            Set<OrderItem> orderItemList = product.getOrderItems();
//            for (OrderItem orderItem : orderItemList) {
//                OrderDeletedEvent orderDeletedEvent = new OrderDeletedEvent(order.getOrderId());
//                orderEventHandler.on(orderDeletedEvent);
//            }
//
//        } catch(Exception e){
//            e.printStackTrace();
//            throw new RuntimeException("Exception in handleOrderConfirmedEvent");
//        }
//    }
}
