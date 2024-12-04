package com.nbloi.cqrses.commonapi.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ConfirmOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;

    // constructor, getters, equals/hashCode and toString

    public ConfirmOrderCommand() {}

    public ConfirmOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
