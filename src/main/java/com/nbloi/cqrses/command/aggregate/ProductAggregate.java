package com.nbloi.cqrses.command.aggregate;

import com.nbloi.cqrses.commonapi.command.CreateProductCommand;
import com.nbloi.cqrses.commonapi.command.ProductInventoryCommand;
import com.nbloi.cqrses.commonapi.event.product.ProductCreatedEvent;
import com.nbloi.cqrses.commonapi.event.product.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.exception.UncreatedOrderException;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Getter
@Setter
@Aggregate
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String name;
    private int stock;
    private BigDecimal price;
    private String currency;
    private String productStatus;
    private boolean orderCreated;
    private boolean productInventoryUpdated;

    public ProductAggregate() {
        // Requested by AXON
    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand command) {
        AggregateLifecycle.apply(new ProductCreatedEvent(
                command.getProductId(),
                command.getName(),
                command.getPrice(),
                command.getStock(),
                command.getCurrency(),
                command.getProductStatus()
        ));
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event){
        this.productId = event.getProductId();
        this.name = event.getName();
        this.stock = event.getStock();
        this.price = event.getPrice();
        this.currency = event.getCurrency();
        this.productStatus = event.getProductStatus();
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
    public void on(ProductInventoryEvent event) {
        this.productId = event.getProductId();
        productInventoryUpdated = true;
    }
}
