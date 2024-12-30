package com.nbloi.cqrses.commonapi.command.customer;

import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
public class MarkCustomerAsDeletedCommand {

    @TargetAggregateIdentifier
    private String customerId;

    public MarkCustomerAsDeletedCommand() {}

    public MarkCustomerAsDeletedCommand(String customerId) {
        this.customerId = customerId;
    }
}
