package com.nbloi.cqrses.commonapi.command;

import com.nbloi.cqrses.commonapi.enums.ProductStatus;
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
    private String productStatus;

    public CreateProductCommand(String productId, String name, BigDecimal price, int stock, String currency) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.currency = currency;
        this.productStatus = ProductStatus.ACTIVE.toString();
    }

    public CreateProductCommand() {}

}
