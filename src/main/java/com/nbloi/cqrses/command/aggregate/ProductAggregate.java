package com.nbloi.cqrses.command.aggregate;

import com.nbloi.cqrses.commonapi.command.CreateOrderItemCommand;
import com.nbloi.cqrses.commonapi.command.ProductInventoryCommand;
import com.nbloi.cqrses.commonapi.event.OrderItemCreatedEvent;
import com.nbloi.cqrses.commonapi.event.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.exception.UncreatedOrderException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateLifecycle;

public class ProductAggregate {

    private boolean orderCreated;
    private String productId;
    private boolean productInventoryUpdated;

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
