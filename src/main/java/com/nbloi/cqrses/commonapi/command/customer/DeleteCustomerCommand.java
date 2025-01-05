package com.nbloi.cqrses.commonapi.command.customer;

import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Getter
@Setter
public class DeleteCustomerCommand {

    @TargetAggregateIdentifier
    private String customerId;
    private String commandId;

    public DeleteCustomerCommand() {}

    public DeleteCustomerCommand(String customerId) {
        this.customerId = customerId;
        this.commandId = UUID.randomUUID().toString();
    }
}
