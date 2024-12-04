package com.nbloi.cqrses.commonapi.event;

import lombok.Getter;

import java.util.Objects;


@Getter
public class OrderConfirmedEvent {

    private String orderId;

    public OrderConfirmedEvent(String orderId) {
        this.orderId = orderId;
    }

    public OrderConfirmedEvent() {}

    public String getOrderId(String orderId) {return orderId;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderConfirmedEvent that = (OrderConfirmedEvent) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId);
    }

    @Override
    public String toString() {
        return "OrderConfirmedEvent{" +
                "orderId='" + orderId + '\'' +
                '}';
    }
}