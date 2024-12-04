package com.nbloi.cqrses.commonapi.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;
    private String productId;

    // constructor, getters, equals/hashCode and toString
        public CreateOrderCommand(String orderId, String productId) {
        this.orderId = orderId;
        this.productId = productId;
    }

    public CreateOrderCommand() {}

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {return productId;}
}