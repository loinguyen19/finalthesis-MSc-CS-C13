package com.nbloi.cqrses.commonapi.command.customer;

import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
public class DeleteCustomerCommand {

    @TargetAggregateIdentifier
    private String customerId;

    public DeleteCustomerCommand() {}

    public DeleteCustomerCommand(String customerId) {
        this.customerId = customerId;
    }
}
