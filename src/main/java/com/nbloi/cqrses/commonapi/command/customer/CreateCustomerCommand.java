package com.nbloi.cqrses.commonapi.command.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.cqrses.commonapi.enums.CustomerStatus;
import com.nbloi.cqrses.query.entity.Customer;
import jakarta.persistence.Column;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateCustomerCommand {

    @TargetAggregateIdentifier
    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private BigDecimal balance;
    private String customerStatus;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    public CreateCustomerCommand() {
        this.createdAt = LocalDateTime.now();
    }

    public CreateCustomerCommand(String customerId, String name, String email, String phoneNumber, BigDecimal balance) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
        this.customerStatus = CustomerStatus.ACTIVE.toString();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {this.customerStatus = customerStatus;}

    //    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }

}