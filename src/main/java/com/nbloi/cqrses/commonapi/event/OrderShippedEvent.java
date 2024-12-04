package com.nbloi.cqrses.commonapi.event;

import lombok.Getter;

import java.util.Objects;

@Getter
public class OrderShippedEvent {

    private String orderId;

    // default constructor, getters, equals/hashCode and toString


    public OrderShippedEvent(String orderId) {
        this.orderId = orderId;
    }

    public OrderShippedEvent() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderShippedEvent that = (OrderShippedEvent) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId);
    }
}