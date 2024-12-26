package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindAllOrdersQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.*;
import com.nbloi.cqrses.query.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerRepository customerRepository;


    public OrderEventHandler(OrderRepository orderRepository, OutboxRepository outboxRepository, ProductRepository productRepository,
                             PaymentRepository paymentRepository, CustomerRepository customerRepository) {
        super();
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        try {
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

                    // check if the product in each order item exists. Then, check if product inventory still has enough stock
                    Product product = productRepository.findById(item.getProduct().getProductId()).orElse(null);
                    if (product == null) {
                        throw new UnfoundEntityException(item.getProduct().getProductId(), Product.class.getSimpleName());
                    } else {
                        if (product.getStock() < item.getQuantity()) {
                            throw new OutOfProductStockException();
                        }
                    }
                    // will update product inventory in ProductInventoryEventConsumer
                    // whenever OrderEventHandler publish the event to broker successfully

                    orderItem.setProduct(product);

                    return orderItem;
                }).collect(Collectors.toSet());

                order.setOrderItems(listOfOrderItems);
                order.setTotalAmount(event.getTotalAmount());
                order.setCurrency(event.getCurrency());

                // Find the customer in database and set them into order
                Customer customer = customerRepository.findById(event.getCustomerId()).orElse(null);
                order.setCustomer(customer);

                // Instantiate a new Payment object and set it with received payment id from order created event
                Payment payment = new Payment();
                    payment.setPaymentId(UUID.randomUUID().toString());
                    payment.setTotalAmount(event.getTotalAmount());
                    payment.setPaymentStatus(PaymentStatus.CREATED);
                    payment.setOrder(order);
                order.setPayment(payment);
                paymentRepository.save(payment);

                // Persist the Order and its OrderItems using the repository
                log.info("Order created: {}", orderId);
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
                LOGGER.info("Sending email for created order {}", orderId);
                mailGateway.sendMail(message);*/
//            }

        } catch (Exception e){
            // Log the error for more specific message
            LOGGER.error("Error handling event: {}", event, e);
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
            LOGGER.error("Error handling event: {}", event, e);
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
            LOGGER.error("Error handling event: {}", event, e);
        }

    }


    @QueryHandler
    public List<Order> handle(FindAllOrdersQuery query) {
        return orderRepository.findAll();
    }

    @QueryHandler
    public Order handle(FindOrderByIdQuery query) {
        return orderRepository.findById(query.getOrderId()).get();
    }

}