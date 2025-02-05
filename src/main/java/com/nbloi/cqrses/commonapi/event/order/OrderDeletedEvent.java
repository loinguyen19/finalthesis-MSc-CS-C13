package com.nbloi.cqrses.commonapi.event.order;

import com.nbloi.cqrses.commonapi.enums.EventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDeletedEvent {

    private String orderId;
    private String type;

    public OrderDeletedEvent(String orderId) {
        this.orderId = orderId;
        this.type = EventType.ORDER_DELETED_EVENT.toString();
    }

    public OrderDeletedEvent() {}
}
