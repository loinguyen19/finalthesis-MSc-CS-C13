package com.nbloi.cqrses.commonapi.event.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerMarkedAsDeletedEvent {

    private String customerId;

    public CustomerMarkedAsDeletedEvent() {}

    public CustomerMarkedAsDeletedEvent(String customerId) {
        this.customerId = customerId;
    }
}
