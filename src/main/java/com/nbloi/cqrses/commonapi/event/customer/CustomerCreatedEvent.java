package com.nbloi.cqrses.commonapi.event.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerCreatedEvent {

    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private BigDecimal balance;
    private String customerStatus;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public CustomerCreatedEvent() {
        this.createdAt = LocalDateTime.now();
    }

    @JsonCreator
    public CustomerCreatedEvent(@JsonProperty("customerId") String customerId,
                                @JsonProperty("name") String name,
                                @JsonProperty("email") String email,
                                @JsonProperty("phoneNumber") String phoneNumber,
                                @JsonProperty("balance") BigDecimal balance,
                                @JsonProperty("createdAt") LocalDateTime createdAt,
                                @JsonProperty("customerStatus") String customerStatus) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.createdAt = createdAt;
        this.customerStatus = customerStatus;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }
}
