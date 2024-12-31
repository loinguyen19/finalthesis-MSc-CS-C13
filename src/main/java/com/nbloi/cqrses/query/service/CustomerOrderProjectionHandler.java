package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.CustomerOrderView;
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
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        Customer customer = customerRepository.findById(event.getCustomerId()).get();

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
        CustomerOrderView view = customerOrderRepository.findById(event.getOrderId()).get();
        view.setOrderStatus(OrderStatus.CONFIRMED.toString());

        customerOrderRepository.save(view);
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        CustomerOrderView view = customerOrderRepository.findById(event.getOrderId()).get();
        view.setOrderStatus(OrderStatus.SHIPPED.toString());

        customerOrderRepository.save(view);
    }
}
