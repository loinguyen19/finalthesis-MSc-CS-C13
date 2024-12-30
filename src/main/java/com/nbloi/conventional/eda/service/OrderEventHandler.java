package com.nbloi.conventional.eda.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.entity.*;
import com.nbloi.conventional.eda.enums.OrderStatus;
import com.nbloi.conventional.eda.enums.PaymentStatus;
import com.nbloi.conventional.eda.event.OrderConfirmedEvent;
import com.nbloi.conventional.eda.event.OrderCreatedEvent;
import com.nbloi.conventional.eda.event.OrderCancelledEvent;
import com.nbloi.conventional.eda.event.OrderShippedEvent;
import com.nbloi.conventional.eda.exception.OutOfProductStockException;
import com.nbloi.conventional.eda.exception.UnconfirmedOrderException;
import com.nbloi.conventional.eda.exception.UnfoundEntityException;
import com.nbloi.conventional.eda.repository.CustomerRepository;
import com.nbloi.conventional.eda.repository.OrderRepository;
import com.nbloi.conventional.eda.repository.PaymentRepository;
import com.nbloi.conventional.eda.repository.ProductRepository;
import com.nbloi.conventional.eda.service.kafkaproducer.OrderCreatedEventProducer;
import lombok.extern.slf4j.Slf4j;
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
    private ProductRepository productRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderCreatedEventProducer orderCreatedEventProducer;


    public OrderEventHandler(OrderRepository orderRepository, ProductRepository productRepository,
                             PaymentRepository paymentRepository, CustomerRepository customerRepository) {
        super();
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    public Order on(OrderCreatedEvent event) {
        Order order = new Order();
        try {
                String orderId = event.getOrderId();

//                Order order = new Order();
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

                String orderCreatedEventPayload = new ObjectMapper().writeValueAsString(event);
                // Publish the order created event into Kafka broker
                orderCreatedEventProducer.sendOrderEvent(orderCreatedEventPayload);
                log.info("Send Order Created Event with payload: {}", orderCreatedEventPayload);

        } catch (Exception e){
            // Log the error for more specific message
            LOGGER.error("Error handling event: {}", event, e);
        }
        return order;
    }


    public void on(OrderConfirmedEvent event) {
        try {
            String orderId = event.getOrderId();
            Order order = orderRepository.findById(orderId).get();

            order.setOrderConfirmedStatus();
            orderRepository.save(order);

            String orderConfirmedEventPayload = new ObjectMapper().writeValueAsString(event);
            // Publish the order confirmed event into Kafka broker
            orderCreatedEventProducer.sendOrderConfirmedEvent(orderConfirmedEventPayload);
            log.info("Send Order Confirmed Event with payload: {}", orderConfirmedEventPayload);
        } catch (Exception e){
            // Log the error for more specific message
            LOGGER.error("Error handling event: {}", event, e);
        }
    }

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

            String orderShippedEventPayload = new ObjectMapper().writeValueAsString(event);
            // Publish the order shipped event into Kafka broker
            orderCreatedEventProducer.sendOrderShippedEvent(orderShippedEventPayload);
            log.info("Send Order Shipped Event with payload: {}", orderShippedEventPayload);
        } catch (Exception e){
            // Log the error for more specific message
            LOGGER.error("Error handling event: {}", event, e);
        }

    }

    public void on(OrderCancelledEvent event) {
        try {
            String orderId = event.getOrderId();
            Order orderToCancelled = readOrderById(orderId);

            if (orderToCancelled != null) {
                if (orderToCancelled.getOrderStatus().equals(OrderStatus.CREATED.toString())) {
                    orderToCancelled.setOrderCancelledStatus();
                    orderRepository.save(orderToCancelled);
                } else {
                    throw new RuntimeException("Order has not been created! Failed to revert order");
                }
            }

        } catch (Exception e){
            // Log the error for more specific message
            LOGGER.error("Error handling event: {}", event, e);
        }

    }


    public List<Order> readAllOrders() {
        return orderRepository.findAll();
    }

    public Order readOrderById(String orderId) {
        return orderRepository.findById(orderId).get();
    }

}