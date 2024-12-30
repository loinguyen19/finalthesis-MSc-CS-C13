package com.nbloi.cqrses.commonapi.command;

import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductCommand {

    @TargetAggregateIdentifier
    private String productId;
    private String name;
    private int stock;
    private BigDecimal price;
    private String currency;

    public CreateProductCommand(String productId, String name, BigDecimal price, int stock, String currency) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.currency = currency;
    }

    public CreateProductCommand() {}

}
