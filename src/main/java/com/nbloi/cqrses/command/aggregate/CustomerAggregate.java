package com.nbloi.cqrses.command.aggregate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.cqrses.commonapi.command.CreateCustomerCommand;
import com.nbloi.cqrses.commonapi.event.CustomerCreatedEvent;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    protected CustomerAggregate() {
        // Required by Axon
    }

    // Aggregate for created order
    @CommandHandler
    public CustomerAggregate(CreateCustomerCommand command) {
        // Avoid heavy computations or synchronous calls here.
        log.info("Handling CreateCustomerCommand for orderId: {}", command.getCustomerId());

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

}