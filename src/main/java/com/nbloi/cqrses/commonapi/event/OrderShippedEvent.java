package com.nbloi.cqrses.commonapi.event;

import lombok.Getter;

import java.util.Objects;

@Getter
public class OrderShippedEvent {

    private String orderItemId;

    // default constructor, getters, equals/hashCode and toString


    public OrderShippedEvent(String orderId) {
        this.orderItemId = orderId;
    }

    public OrderShippedEvent() {}



    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderShippedEvent that = (OrderShippedEvent) o;
        return Objects.equals(orderItemId, that.orderItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderItemId);
    }
}