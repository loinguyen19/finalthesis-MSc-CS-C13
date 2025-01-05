package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.*;
import com.nbloi.cqrses.commonapi.event.order.*;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindAllOrdersQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByCustomerQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.customer.FindCustomerByIdQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.*;
import com.nbloi.cqrses.query.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@ProcessingGroup("orderProcessor")
public class OrderEventHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private ProductEventHandler productEventHandler;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerEventHandler customerEventHandler;


    public OrderEventHandler(OrderRepository orderRepository, OutboxRepository outboxRepository, ProductEventHandler productEventHandler,
                             PaymentRepository paymentRepository, CustomerEventHandler customerEventHandler) {
        super();
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.productEventHandler = productEventHandler;
        this.paymentRepository = paymentRepository;
        this.customerEventHandler = customerEventHandler;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        try {
                String orderId = event.getOrderId();

                Order order = new Order();
                order.setOrderId(orderId);
                order.setOrderCreatedStatus(OrderStatus.CREATED.toString());

                Set<OrderItem> listOfOrderItems = event.getOrderItems().stream().map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderItemId(item.getOrderItemId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setTotalPrice(item.getTotalPrice());
                    orderItem.setCurrency(item.getCurrency());
                    orderItem.setOrder(order); // Properly assign the parent order

                    // check if the product in each order item exists. Then, check if product inventory still has enough stock
                    Product product = productEventHandler.handle(new FindProductByIdQuery(item.getProduct().getProductId()));
                    if (product == null) {
                        throw new UnfoundEntityException(item.getProduct().getProductId(), Product.class.getSimpleName());
                    } else {
                        if (product.getStock() < item.getQuantity()) {
                            throw new OutOfProductStockException();
                        }
//                        product.setStock(product.getStock() - item.getQuantity());
                    }

                    orderItem.setProduct(product);

                    return orderItem;
                }).collect(Collectors.toSet());

                order.setOrderItems(listOfOrderItems);
                order.setTotalAmount(event.getTotalAmount());
                order.setCurrency(event.getCurrency());

                // Find the customer in database and set them into order
                Customer customer = customerEventHandler.handle(new FindCustomerByIdQuery(event.getCustomerId()));
                order.setCustomer(customer);

                // Instantiate a new Payment object and set it with received payment id from order created event
                Payment payment = new Payment();
                    payment.setPaymentId(event.getPaymentId());
                    payment.setTotalAmount(event.getTotalAmount());
                    payment.setPaymentStatus(PaymentStatus.CREATED.toString());
                    payment.setOrderId(orderId);
                    payment.setCurrency(event.getCurrency());
                    payment.setOrder(order);
                order.setPayment(payment);
                paymentRepository.save(payment);

                // Persist the Order and its OrderItems using the repository
                log.info("Order created with ID: {}", orderId);
                orderRepository.save(order);

                // Save Outbox Message
                OutboxMessage outboxMessage = new OutboxMessage(
                        UUID.randomUUID().toString(),
                        event.getOrderId(),
                        EventType.ORDER_CREATED_EVENT.toString(),
                        new ObjectMapper().writeValueAsString(event),
//                        serializeEvent(event),  // Serialize the event
                        OutboxStatus.PENDING.toString()
                );
                outboxRepository.save(outboxMessage);
                log.info("Processing 'created order' OutboxMessage with payload: {}", outboxMessage.getPayload());

/*                // Publish the event into Kafka broker
                orderCreatedEventProducer.sendOrderEvent(event);*/

/*                // send message to outbox when write database already completes
                MailMessage message = new MailMessage("Order %s created".formatted(orderId),
                        "Your order is marked as 'created' in our system and will be processed to confirmed status.",
                        "orderCreated");
                log.info("Sending email for created order {}", orderId);
                mailGateway.sendMail(message);*/
//            }

        } catch (Exception e){
            // Log the error for more specific message
            log.error("Error handling event: {}", event, e);
        }

    }


    @EventHandler
    public void on(OrderConfirmedEvent event) {
        try {
            String orderId = event.getOrderId();
            Order order = orderRepository.findById(orderId).get();

            order.setOrderConfirmedStatus();
            orderRepository.save(order);

            // TODO: send message to transactional outbox pattern to manage the event state before sending to Kafka broker
            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(
                    UUID.randomUUID().toString(),
                    event.getOrderId(),
                    EventType.ORDER_CONFIRMED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(event),
                    OutboxStatus.PENDING.toString()
            );
            outboxRepository.save(outboxMessage);
            log.info("Processing 'confirmed order' OutboxMessage with payload: {}", outboxMessage.getPayload());
        } catch (Exception e){
            // Log the error for more specific message
            log.error("Error handling event: {}", event, e);
        }
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        try {
            String orderId = event.getOrderId();
            Order orderToShip = orderRepository.findById(orderId).get();

            if (orderToShip.getOrderStatus().toString().equals(OrderStatus.CONFIRMED.toString())) {
                orderToShip.setOrderShippedStatus();
            } else {
                throw new UnconfirmedOrderException();
            }

            orderRepository.save(orderToShip);

            // TODO: send message to transactional outbox pattern to manage the event state before sending to Kafka broker
            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(
                    UUID.randomUUID().toString(),
                    event.getOrderId(),
                    EventType.ORDER_SHIPPED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(event),
                    OutboxStatus.PENDING.toString()
            );
            outboxRepository.save(outboxMessage);
            log.info("Processing 'shipped order' OutboxMessage with payload: {}", outboxMessage.getPayload());
        } catch (Exception e){
            // Log the error for more specific message
            log.error("Error handling event: {}", event, e);
        }

    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        try {
            String orderId = event.getOrderId();
            Order orderToCancelled = handle(new FindOrderByIdQuery(orderId));
            if (orderToCancelled != null) {
//            Order orderToCancelled = orderRepository.findById(orderId).get();

                if (orderToCancelled.getOrderStatus().equals(OrderStatus.CREATED.toString())) {
                    orderToCancelled.setOrderCancelledStatus();
                } else {
                    throw new RuntimeException("Order has not been created! Failed to revert order");
                }
            }

            orderRepository.save(orderToCancelled);

            // TODO: send message to transactional outbox pattern to manage the event state before sending to Kafka broker
            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(
                    UUID.randomUUID().toString(),
                    event.getOrderId(),
                    EventType.ORDER_CANCELLED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(event),
                    OutboxStatus.PENDING.toString()
            );
            outboxRepository.save(outboxMessage);
            log.info("Processing 'failed to be paid order' OutboxMessage with payload: {}", outboxMessage.getPayload());
        } catch (Exception e){
            // Log the error for more specific message
            log.error("Error handling event: {}", event, e);
        }

    }

    @EventHandler
    public void on(OrderDeletedEvent event) {
        try {
            String orderId = event.getOrderId();
            Order orderToDelete = handle(new FindOrderByIdQuery(orderId));

            orderRepository.delete(orderToDelete);

            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(
                    UUID.randomUUID().toString(),
                    event.getOrderId(),
                    EventType.ORDER_DELETED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(event),
                    OutboxStatus.PENDING.toString()
            );
            outboxRepository.save(outboxMessage);
            log.info("Processing Deleted Event OutboxMessage with payload: {}", outboxMessage.getPayload());
        } catch (Exception e){
            // Log the error for more specific message
            log.error("Error handling event: {}", event, e);
        }

    }


    @QueryHandler
    public List<Order> handle(FindAllOrdersQuery query) {
        return orderRepository.findAll();
    }

    @QueryHandler
    public Order handle(FindOrderByIdQuery query) {
        Order order = orderRepository.findById(query.getOrderId()).orElse(null);
        if (order == null) {throw new UnfoundEntityException(query.getOrderId(), Order.class.getSimpleName());}
        return order;
    }

    @QueryHandler
    public List<Order> handle(FindOrderByCustomerQuery query) {
        return orderRepository.findByCustomer(query.getCustomer());
    }
}