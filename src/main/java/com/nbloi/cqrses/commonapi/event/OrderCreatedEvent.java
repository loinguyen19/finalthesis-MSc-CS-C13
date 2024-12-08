package com.nbloi.cqrses.commonapi.event;

import java.util.List;
import java.util.Objects;

public class OrderCreatedEvent {

    private String orderId;
    private String productId;
    private int quantity;
    private double amount;
    private String currency;


    public OrderCreatedEvent(String orderId, String productId, int quantity, double amount, String currency) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.currency = currency;
    }

    public OrderCreatedEvent() {}

    public String getOrderItemId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {return quantity;}

    public double getAmount() {return amount;}

    public String getCurrency() {return currency;}


    public void setOrderItemId(String orderId) {
        this.orderId = orderId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {this.quantity = quantity;}

    public void setAmount(double amount) {this.amount = amount;}

    public void setCurrency(String currency) {this.currency = currency;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return quantity == that.quantity && Double.compare(amount, that.amount) == 0 && Objects.equals(orderId, that.orderId) && Objects.equals(productId, that.productId) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId, quantity, amount, currency);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
