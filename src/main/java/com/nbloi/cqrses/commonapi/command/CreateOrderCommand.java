package com.nbloi.cqrses.commonapi.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private String orderItemId;
    private String productId;
    private int quantity;
    private double amount;
    private String currency;

    // constructor, getters, equals/hashCode and toString
        public CreateOrderCommand(String orderItemId, String productId, int quantity, double amount, String currency) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.currency = currency;
    }

    public CreateOrderCommand() {}

    public String getOrderId() {
        return orderItemId;
    }

    public String getProductId() {return productId;}

    public int getQuantity() {return quantity;}

    public double getAmount() {return amount;}
    public String getCurrency() {return currency;}
}