package com.nbloi.cqrses.commonapi.event;

import java.util.List;
import java.util.Objects;

public class OrderCreatedEvent {

    private String orderItemId;
    private String productId;
    private int quantity;


    public OrderCreatedEvent(String orderItemId, String productId, int quantity) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderCreatedEvent() {}

    public String getOrderItemId() {
        return orderItemId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {return quantity;}

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {this.quantity = quantity;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return quantity == that.quantity && Objects.equals(orderItemId, that.orderItemId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderItemId, productId, quantity);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderItemId='" + orderItemId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
