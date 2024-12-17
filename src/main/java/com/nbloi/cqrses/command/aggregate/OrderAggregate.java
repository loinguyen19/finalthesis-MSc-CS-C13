package com.nbloi.cqrses.command.aggregate;

import com.nbloi.cqrses.commonapi.command.*;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.event.*;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import com.nbloi.cqrses.commonapi.exception.UncreatedOrderException;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private List<OrderItem> orderItems;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private boolean orderConfirmed;
    private String customerId;
    private String productId;

    protected OrderAggregate() {
        // Required by Axon
    }

    // Aggregate for created order
    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        AggregateLifecycle.apply(new OrderCreatedEvent(
                command.getOrderId(),
                command.getOrderItems(),
                command.getOrderStatus(),
                command.getTotalAmount(),
                command.getCustomerId(),
                command.getPaymentId()
        ));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.orderItems = event.getOrderItems();
        this.orderStatus = event.getOrderStatus();
        this.totalAmount = event.getTotalAmount();
        this.customerId = event.getCustomerId();
        this.productId = event.getPaymentId();
        orderConfirmed = false;
    }

    // Aggregate Command Handlers for confirmed and shipped orders
    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        if (orderConfirmed) {
            return;
        }
        AggregateLifecycle.apply(new OrderConfirmedEvent(orderId));
    }

    @CommandHandler
    public void handle(ShipOrderCommand command) {
        if (!orderConfirmed) {
            throw new UnconfirmedOrderException();
        }
        AggregateLifecycle.apply(new OrderShippedEvent(orderId));
    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        orderConfirmed = true;
    }


    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public boolean isOrderConfirmed() {
        return orderConfirmed;
    }
}