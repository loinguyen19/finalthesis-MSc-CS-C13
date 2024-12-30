package com.nbloi.cqrses.command.aggregate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.cqrses.command.controller.CustomerController;
import com.nbloi.cqrses.commonapi.command.customer.CreateCustomerCommand;
import com.nbloi.cqrses.commonapi.command.customer.DeleteCustomerCommand;
import com.nbloi.cqrses.commonapi.command.customer.UpdateCustomerCommand;
import com.nbloi.cqrses.commonapi.event.customer.CustomerCreatedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerDeletedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerUpdatedEvent;
import com.nbloi.cqrses.query.service.QueryModelValidator;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Aggregate
@Slf4j
public class CustomerAggregate {

    @AggregateIdentifier
    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private QueryModelValidator queryModelValidator;

    protected CustomerAggregate() {
        // Required by Axon
    }

    // Aggregate for created customer
    @CommandHandler
    public CustomerAggregate(CreateCustomerCommand command) {
        // Avoid heavy computations or synchronous calls here.
        log.info("Handling CreateCustomerCommand for customerId: {}", command.getCustomerId());

        AggregateLifecycle.apply(new CustomerCreatedEvent(
                command.getCustomerId(),
                command.getName(),
                command.getEmail(),
                command.getPhoneNumber(),
                command.getBalance(),
                command.getCreatedAt()
        ));
    }

    @EventSourcingHandler
    public void on(CustomerCreatedEvent event) {
        this.customerId = event.getCustomerId();
        this.name = event.getName();
        this.email = event.getEmail();
        this.phoneNumber = event.getPhoneNumber();
        this.balance = event.getBalance();
        this.createdAt = event.getCreatedAt();
    }

    // Aggregate for updated customer
    @CommandHandler
    public void handle(UpdateCustomerCommand command) {
        // Validate if customer already exists on database
        if (this.customerId == null) {
            throw new IllegalStateException("Customer does not exist: " + command.getCustomerId());
        }
        // Avoid heavy computations or synchronous calls here.
        log.info("Handling UpdateCustomerCommand for customerId: {}", command.getCustomerId());

        AggregateLifecycle.apply(new CustomerUpdatedEvent(
                command.getCustomerId(),
                command.getName(),
                command.getEmail(),
                command.getPhoneNumber(),
                command.getBalance()
        ));
    }

    @EventSourcingHandler
    public void on(CustomerUpdatedEvent event) {
        this.customerId = event.getCustomerId();
        this.name = event.getName();
        this.email = event.getEmail();
        this.phoneNumber = event.getPhoneNumber();
        this.balance = event.getBalance();
    }

    // Aggregate for deleted customer
    @CommandHandler
    public void handle(DeleteCustomerCommand command) {
        if (!queryModelValidator.aggregateExists(command.getCustomerId())) {
            CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent();
            customerCreatedEvent.setCustomerId(command.getCustomerId());
            AggregateLifecycle.apply(
                    customerCreatedEvent);
                     //"Legacy order" // placeholder data);
        }
        // Avoid heavy computations or synchronous calls here.
        log.info("Handling DeleteCustomerCommand for customerId: {}", command.getCustomerId());

        AggregateLifecycle.apply(new CustomerDeletedEvent(command.getCustomerId()));
    }

    @EventSourcingHandler
    public void on(CustomerDeletedEvent event) {
        this.customerId = null;
    }
}