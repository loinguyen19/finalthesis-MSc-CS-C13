package com.nbloi.cqrses.commonapi.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public class ProductInventoryCommand {

    @TargetAggregateIdentifier
    private String productId;
    private String name;
    private int stock;
    private BigDecimal price;
    private String currency;

    public ProductInventoryCommand(String name, int Stock, String productId, BigDecimal price, String currency) {
        this.productId = productId;
        this.name = name;
        this.stock = Stock;
        this.price = price;
        this.currency = currency;
    }

    public ProductInventoryCommand() {}

    public String getProductId() {return productId;}
    public String getName() {return name;}
    public int getStock() {return stock;}
    public BigDecimal getPrice() {return price;}
    public String getCurrency() {return currency;}


    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrice(BigDecimal price) {this.price = price;}

    public void setCurrency(String currency) {this.currency = currency;}

}
