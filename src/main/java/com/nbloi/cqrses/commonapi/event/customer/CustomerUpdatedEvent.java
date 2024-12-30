package com.nbloi.cqrses.commonapi.event.customer;

import java.math.BigDecimal;

public class CustomerUpdatedEvent {

    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private BigDecimal balance;

    public CustomerUpdatedEvent() {
    }

    public CustomerUpdatedEvent(String customerId, String name, String email, String phoneNumber, BigDecimal balance) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
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

}
