package com.nbloi.cqrses.commonapi.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private String orderItemId;
    private String productId;
    private int quantity;

    // constructor, getters, equals/hashCode and toString
        public CreateOrderCommand(String orderItemId, String productId, int quantity) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public CreateOrderCommand() {}

    public String getOrderId() {
        return orderItemId;
    }

    public String getProductId() {return productId;}

    public int getQuantity() {return quantity;}
}