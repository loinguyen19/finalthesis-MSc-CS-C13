package com.nbloi.cqrses.commonapi.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ConfirmOrderCommand {

    @TargetAggregateIdentifier
    private String orderItemId;

    // constructor, getters, equals/hashCode and toString

    public ConfirmOrderCommand() {}

    public ConfirmOrderCommand(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getOrderId() {
        return orderItemId;
    }
}
