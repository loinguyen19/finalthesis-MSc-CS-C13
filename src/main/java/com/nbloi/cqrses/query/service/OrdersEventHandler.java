package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import com.nbloi.cqrses.commonapi.query.FindAllOrderedProductsQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.OrderDetails;
import com.nbloi.cqrses.query.repository.OrderRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class OrdersEventHandler {

    @Autowired
    private OrderRepository orderRepository;

    private final Map<String, OrderDetails> orders = new HashMap<>();

    public OrdersEventHandler(OrderRepository orderRepository) {
        super();
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        String orderId = event.getOrderId();
        orders.put(orderId, new OrderDetails(orderId, event.getProductId()));
        orderRepository.save(orders.get(orderId));
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        String orderId = event.getOrderId();
        OrderDetails orderDetailsToConfirm = orderRepository.findById(orderId).get();

        orderDetailsToConfirm.setOrderStatus(OrderStatus.CONFIRMED);
        orders.put(orderId, orderDetailsToConfirm);
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        String orderId = event.getOrderId();
        OrderDetails orderDetailsToShip = orderRepository.findById(orderId).get();

        if (orderDetailsToShip.getOrderStatus() == OrderStatus.CONFIRMED) {
            orderDetailsToShip.setOrderStatus(OrderStatus.SHIPPED);
        } else {
            throw new UnconfirmedOrderException();
        }

        orders.put(orderId, orderDetailsToShip);
        orderRepository.save(orders.get(orderId));
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