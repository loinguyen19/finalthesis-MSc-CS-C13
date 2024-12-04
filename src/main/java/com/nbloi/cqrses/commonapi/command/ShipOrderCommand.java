package com.nbloi.cqrses.commonapi.command;

import com.nbloi.cqrses.commonapi.dto.ShipOrderRequestDTO;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ShipOrderCommand {

    @TargetAggregateIdentifier
    private String orderItemId;

    // constructor, getters, equals/hashCode and toString

    public ShipOrderCommand(){}

    public ShipOrderCommand(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getOrderId() {return orderItemId;}
}