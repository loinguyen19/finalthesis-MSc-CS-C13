package com.nbloi.cqrses.command.aggregate;

import com.nbloi.cqrses.commonapi.command.CreateProductCommand;
import com.nbloi.cqrses.commonapi.command.DeleteProductCommand;
import com.nbloi.cqrses.commonapi.command.ProductInventoryCommand;
import com.nbloi.cqrses.commonapi.enums.ProductStatus;
import com.nbloi.cqrses.commonapi.event.product.ProductCreatedEvent;
import com.nbloi.cqrses.commonapi.event.product.ProductDeletedEvent;
import com.nbloi.cqrses.commonapi.event.product.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.exception.UncreatedOrderException;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.query.entity.Product;
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
    private boolean productCreated;
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
    public void handle(DeleteProductCommand command) {
        AggregateLifecycle.apply(new ProductDeletedEvent(
                command.getProductId()
        ));
    }

    @EventSourcingHandler
    public void on(ProductDeletedEvent event){
        this.productId = event.getProductId();
        this.productStatus = ProductStatus.DELETED.toString();
    }

    @CommandHandler
    public void handle(ProductInventoryCommand command) {
        if (!productCreated){
            throw new UnfoundEntityException(command.getProductId(), Product.class.getSimpleName());
        }
        AggregateLifecycle.apply(new ProductInventoryEvent(command.getProductId(), command.getName(),
                command.getStock(), command.getPrice(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(ProductInventoryEvent event) {
        this.productId = event.getProductId();
        this.name = event.getName();
        this.stock = event.getStock();
        this.price = event.getPrice();
        this.currency = event.getCurrency();
        this.productStatus = ProductStatus.UPDATED.toString();
        productInventoryUpdated = true;
    }
}
