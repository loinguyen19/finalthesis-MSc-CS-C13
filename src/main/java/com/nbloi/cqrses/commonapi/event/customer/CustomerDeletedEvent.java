package com.nbloi.cqrses.commonapi.event.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CustomerDeletedEvent {

    private String customerId;
    private String customerDeletedEventId;

    public CustomerDeletedEvent() {}

    public CustomerDeletedEvent(String customerId) {
        this.customerId = customerId;
        this.customerDeletedEventId = UUID.randomUUID().toString();
    }
}
