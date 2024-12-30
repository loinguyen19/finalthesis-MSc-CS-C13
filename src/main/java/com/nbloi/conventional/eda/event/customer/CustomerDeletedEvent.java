package com.nbloi.conventional.eda.event.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDeletedEvent {

    private String customerId;

    public CustomerDeletedEvent() {}

    public CustomerDeletedEvent(String customerId) {
        this.customerId = customerId;
    }
}
