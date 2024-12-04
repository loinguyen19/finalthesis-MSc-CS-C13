package com.nbloi.cqrses.commonapi.dto;

import java.util.List;

public class CreateOrderRequestDTO {

    private String orderId;
    private String productId;
    private int quantity;
    private double amount;

    // constructor, getters, equals/hashCode and toString
    public CreateOrderRequestDTO(String orderId, String productId, int quantity, double amount) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
    }

    public CreateOrderRequestDTO() {}

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {return productId;}

    public int getQuantity() {return quantity;}

    public double getAmount() {return amount;}
}
