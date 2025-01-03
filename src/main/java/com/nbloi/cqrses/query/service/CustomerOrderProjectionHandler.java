package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.event.order.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.order.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.order.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.customer.FindCustomerByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.CustomerOrderView;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.repository.CustomerOrderRepository;
import com.nbloi.cqrses.query.repository.CustomerRepository;
import com.nbloi.cqrses.query.repository.OrderRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
public class CustomerOrderProjectionHandler {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private CustomerEventHandler customerEventHandler;

    @Autowired
    private OrderEventHandler orderEventHandler;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        Customer customer = customerEventHandler.handle(new FindCustomerByIdQuery(event.getCustomerId()));

        CustomerOrderView customerOrderView = new CustomerOrderView();
        customerOrderView.setCustomerOrderViewId(UUID.randomUUID().toString());
        customerOrderView.setCustomerId(event.getCustomerId());
        customerOrderView.setOrderId(event.getOrderId());
        customerOrderView.setOrderStatus(event.getOrderStatus());
        customerOrderView.setOrderDate(event.getCreatedAt());
        customerOrderView.setCustomerName(customer.getName());
        customerOrderView.setTotalOrderAmount(event.getTotalAmount());

        customerOrderRepository.save(customerOrderView);

    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        Order order = orderEventHandler.handle(new FindOrderByIdQuery(event.getOrderId()));
        String orderId = order.getOrderId();

        Customer customer = order.getCustomer();
        String customerId = customer.getCustomerId();
        CustomerOrderView view = customerOrderRepository.findByCustomerIdAndOrderId(customerId, orderId);
        view.setOrderStatus(OrderStatus.CONFIRMED.toString());

        customerOrderRepository.save(view);
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        Order order = orderEventHandler.handle(new FindOrderByIdQuery(event.getOrderId()));
        String orderId = order.getOrderId();

        Customer customer = order.getCustomer();
        String customerId = customer.getCustomerId();
        CustomerOrderView view = customerOrderRepository.findByCustomerIdAndOrderId(customerId, orderId);
        view.setOrderStatus(OrderStatus.SHIPPED.toString());

        customerOrderRepository.save(view);
    }
}
