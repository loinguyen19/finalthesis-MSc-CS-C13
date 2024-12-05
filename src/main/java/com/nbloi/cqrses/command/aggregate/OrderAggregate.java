package com.nbloi.cqrses.command.aggregate;

import com.nbloi.cqrses.commonapi.command.ConfirmOrderCommand;
import com.nbloi.cqrses.commonapi.command.CreateOrderCommand;
import com.nbloi.cqrses.commonapi.command.ProductInventoryCommand;
import com.nbloi.cqrses.commonapi.command.ShipOrderCommand;
import com.nbloi.cqrses.commonapi.event.*;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import com.nbloi.cqrses.commonapi.exception.UncreatedOrderException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderItemId;
    private boolean orderConfirmed;
    private boolean orderCreated;
    private String productId;
    private boolean productInventoryUpdated;

    protected OrderAggregate() {}

    // Aggregate for created order
    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        AggregateLifecycle.apply(new OrderCreatedEvent(command.getOrderId(), command.getProductId(),
                command.getQuantity(), command.getAmount(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderItemId = event.getOrderItemId();
        orderConfirmed = true;
        orderConfirmed = false;
    }

    // Aggregate Command Handlers for confirmed and shipped orders
    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        if (orderConfirmed) {
            return;
        }
        AggregateLifecycle.apply(new OrderConfirmedEvent(orderItemId));
    }

    @CommandHandler
    public void handle(ShipOrderCommand command) {
        if (!orderConfirmed) {
            throw new UnconfirmedOrderException();
        }
        AggregateLifecycle.apply(new OrderShippedEvent(orderItemId));
    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        orderConfirmed = true;
    }


    @CommandHandler
    public void handle(ProductInventoryCommand command) {
        if (!orderCreated){
            throw new UncreatedOrderException();
        }
        AggregateLifecycle.apply(new ProductInventoryEvent(command.getProductId(), command.getName(),
                command.getStock(), command.getPrice(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(ProductInventoryCommand event) {
        this.productId = event.getProductId();
        productInventoryUpdated = true;
    }

}