package com.nbloi.cqrses.command.aggregate;

import com.nbloi.cqrses.commonapi.command.*;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.event.*;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import com.nbloi.cqrses.commonapi.exception.UncreatedOrderException;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private List<OrderItem> orderItems;
    private String orderStatus;
    private BigDecimal totalAmount;
    private boolean orderConfirmed;
    private String customerId;
    private String paymentId;
    private String currency;

    protected OrderAggregate() {
        // Required by Axon
    }

    // Aggregate for created order
    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        // Avoid heavy computations or synchronous calls here.
        log.info("Handling CreateOrderCommand for orderId: {}", command.getOrderId());

        AggregateLifecycle.apply(new OrderCreatedEvent(
                command.getOrderId(),
                command.getOrderItems(),
                command.getOrderStatus(),
                command.getTotalAmount(),
                command.getCurrency(),
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
        this.currency = event.getCurrency();
        this.customerId = event.getCustomerId();
        this.paymentId = event.getPaymentId();
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public boolean isOrderConfirmed() {
        return orderConfirmed;
    }

    public String getCustomerId() {return customerId;}

    public String getPaymentId() {return paymentId;}

    public String getCurrency() {return currency;}

}