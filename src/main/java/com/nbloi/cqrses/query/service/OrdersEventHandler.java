package com.nbloi.cqrses.query.service;

import com.google.common.base.Strings;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import com.nbloi.cqrses.commonapi.query.FindAllOrderedProductsQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.OrderDetails;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.kafkaproducer.OrderCreatedEventProducer;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
//import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class OrdersEventHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductInventoryEventHandler productInventoryEventHandler;

    // inject OrderCreatedEventProducer to send the event to Kafka broker
    @Autowired
    private OrderCreatedEventProducer orderCreatedEventProducer;

//    private final Map<String, OrderDetails> orders = new HashMap<>();

    public OrdersEventHandler(OrderRepository orderRepository) {
        super();
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        String orderId = event.getOrderItemId();
//        orders.put(orderId, new OrderDetails(orderId, event.getProductId()));
        OrderDetails orderCreated = new OrderDetails();
        orderCreated.setOrderItemId(orderId);
        orderCreated.setOrderStatus(OrderStatus.CREATED);
        orderCreated.setAmount(event.getAmount());
        orderCreated.setCurrency(event.getCurrency());
        orderCreated.setQuantity(event.getQuantity());

        Product product = productInventoryEventHandler.handle(new FindProductByIdQuery(event.getProductId()));
        orderCreated.setProduct(product);

        orderRepository.save(orderCreated);

        orderCreatedEventProducer.sendOrderEvent(event);

    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        String orderId = event.getOrderItemId();
        OrderDetails orderDetailsToConfirm = orderRepository.findById(orderId).get();

        orderDetailsToConfirm.setOrderConfirmed();
        orderRepository.save(orderDetailsToConfirm);
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        String orderId = event.getOrderItemId();
        OrderDetails orderDetailsToShip = orderRepository.findById(orderId).get();

        if (orderDetailsToShip.getOrderStatus().equals(OrderStatus.CONFIRMED.toString())) {
            orderDetailsToShip.setOrderStatus(OrderStatus.SHIPPED);
        } else {
            throw new UnconfirmedOrderException();
        }

        orderRepository.save(orderDetailsToShip);
    }

    // Event Handlers for OrderConfirmedEvent and OrderShippedEvent...

    @QueryHandler
    public List<OrderDetails> handle(FindAllOrderedProductsQuery query) {
        return orderRepository.findAll();
    }

    @QueryHandler
    public OrderDetails handle(FindOrderByIdQuery query) {
        return orderRepository.findById(query.getOrderId()).get();
    }

}