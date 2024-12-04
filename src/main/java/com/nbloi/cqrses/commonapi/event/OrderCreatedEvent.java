package com.nbloi.cqrses.commonapi.event;

import java.util.Objects;

public class OrderCreatedEvent {

    private String orderId;
    private String productId;


    public OrderCreatedEvent(String orderId, String productId) {
        this.orderId = orderId;
        this.productId = productId;
    }

    public OrderCreatedEvent() {}

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}
