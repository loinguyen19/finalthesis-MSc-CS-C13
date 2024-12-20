package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import com.nbloi.cqrses.commonapi.query.FindAllOrdersQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.*;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class OrderEventHandler {

    @Autowired
    private OrderRepository orderRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventHandler.class);

    @Autowired
    private OutboxRepository outboxRepository;


    public OrderEventHandler(OrderRepository orderRepository, OutboxRepository outboxRepository) {
        super();
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        try {
//            if ("problematicEventId".equals(event.getOrderId())) {
//                log.warn("Skipping problematic event: {}", event.getOrderId());
//                return;
//            }
//            else if ("UUID-OT-1".equals(event.getOrderId())) {
//                // Skip this specific event
//                return;
//            }
//            else {
                String orderId = event.getOrderId();

                Order order = new Order();
                order.setOrderId(orderId);
                order.setOrderCreatedStatus(OrderStatus.CREATED);

                Set<OrderItem> listOfOrderItems = event.getOrderItems().stream().map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderItemId(item.getOrderItemId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setTotalPrice(item.getTotalPrice());
                    orderItem.setCurrency(item.getCurrency());
                    orderItem.setOrder(order); // Properly assign the parent order
                    orderItem.setProduct(item.getProduct());
//                    orderItem.setProduct(productInventoryEventHandler.handle(new FindProductByIdQuery(item.getProduct().getProductId())));
                    return orderItem;
                }).collect(Collectors.toSet());

                order.setOrderItems(listOfOrderItems);
                order.setTotalAmount(event.getTotalAmount());

                // Instantiate a new Customer object and set it with received customer id from order created event
                Customer customer = new Customer();
                customer.setCustomerId(event.getCustomerId());
                order.setCustomer(customer);

                // Instantiate a new Payment object and set it with received payment id from order created event
                Payment payment = new Payment();
                payment.setPaymentId(event.getPaymentId());
                payment.setPaymentStatus(PaymentStatus.NEW);
                payment.setOrder(order);

                order.setPayment(payment);

                // Persist the Order and its OrderItems using the repository
                orderRepository.save(order);

                // Save Outbox Message
                OutboxMessage outboxMessage = new OutboxMessage(
                        UUID.randomUUID().toString(),
                        event.getOrderId(),
                        event.getClass().getSimpleName(),
                        new ObjectMapper().writeValueAsString(event),
//                        serializeEvent(event),  // Serialize the event
                        OutboxStatus.PENDING.toString()
                );
                outboxRepository.save(outboxMessage);
                log.info("Processing OutboxMessage with payload: {}", outboxMessage.getPayload());

//            for (OrderItem o : listOfOrderItems) {
//                Product productFoundById = productInventoryEventHandler.handle(new FindProductByIdQuery(o.getProduct().getProductId()));
//                if (productFoundById.equals(new Product())) {
//                    throw new UnfoundEntityException(o.getProduct().getProductId(), "Product");
//                } else {
//                    productFoundById.setStock(productFoundById.getStock() - o.getQuantity());
//
//                    // Save the update stock of each product
//                    productRepository.save(productFoundById);
//                }
//            }

/*                // Publish the event into Kafka broker
                orderCreatedEventProducer.sendOrderEvent(event);*/

/*                // send message to outbox when write database already completes
                MailMessage message = new MailMessage("Order %s created".formatted(orderId),
                        "Your order is marked as 'created' in our system and will be processed to confirmed status.",
                        "orderCreated");
                LOGGER.info("Sending email for created order {}", orderId);
                mailGateway.sendMail(message);*/
//            }

        } catch (Exception e){
            // Log the error and take appropriate action
            LOGGER.error("Error handling event: {}", event, e);
        }

    }


    @EventHandler
    public void on(OrderConfirmedEvent event) {
        String orderId = event.getOrderId();
        Order orderItemToConfirm = orderRepository.findById(orderId).get();

        orderItemToConfirm.setOrderConfirmedStatus();
        orderRepository.save(orderItemToConfirm);

/*        // send message to outbox when write database already completes
        MailMessage message = new MailMessage("Order %s confirmed".formatted(orderId),
                "Your order is marked as 'confirmed' in our system and will be moved to delivery service.",
                "orderConfirmed");
        LOGGER.info("Sending email for confirmed order {}", orderId);
        mailGateway.sendMail(message);*/
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        String orderId = event.getOrderId();
        Order orderToShip = orderRepository.findById(orderId).get();

        if (orderToShip.getOrderStatus().equals(OrderStatus.CONFIRMED.toString())) {
            orderToShip.setOrderShippedStatus();
        } else {
            throw new UnconfirmedOrderException();
        }

        orderRepository.save(orderToShip);

/*        // send message to outbox when write database already completes
        MailMessage message = new MailMessage("Order %s shipped".formatted(orderId),
                "Your order is marked as 'shipped' in our system and will be on the way to ship.",
                "orderShipped");
        LOGGER.info("Sending email for shipped order {}", orderId);
        mailGateway.sendMail(message);*/
    }

    // Event Handlers for OrderConfirmedEvent and OrderShippedEvent...

    @QueryHandler
    public List<Order> handle(FindAllOrdersQuery query) {
        return orderRepository.findAll();
    }

    @QueryHandler
    public Order handle(FindOrderByIdQuery query) {
        return orderRepository.findById(query.getOrderId()).get();
    }


    private String serializeEvent(Object event) {
        try {
            return new ObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

}