package com.nbloi.cqrses.commonapi.event;

import lombok.Getter;

import java.util.Objects;


@Getter
public class OrderConfirmedEvent {

    private String orderItemId;

    public OrderConfirmedEvent(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public OrderConfirmedEvent() {}

    public String getOrderItemId(String orderItemId) {return orderItemId;}

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderConfirmedEvent that = (OrderConfirmedEvent) o;
        return Objects.equals(orderItemId, that.orderItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderItemId);
    }

    @Override
    public String toString() {
        return "OrderConfirmedEvent{" +
                "orderItemId='" + orderItemId + '\'' +
                '}';
    }
}