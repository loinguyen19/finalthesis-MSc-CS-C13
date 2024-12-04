package com.nbloi.cqrses.commonapi.command;

import com.nbloi.cqrses.commonapi.dto.ShipOrderRequestDTO;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ShipOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;

    // constructor, getters, equals/hashCode and toString

    public ShipOrderCommand(){}

    public ShipOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {return orderId;}
}