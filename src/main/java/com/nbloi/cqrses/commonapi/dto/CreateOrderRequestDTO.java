package com.nbloi.cqrses.commonapi.dto;

public class CreateOrderRequestDTO {

    private String orderItemId;
    private String productId;
    private int quantity;
    private double amount;
    private String currency;

    // constructor, getters, equals/hashCode and toString
    public CreateOrderRequestDTO(String orderItemId, String productId, int quantity, double amount, String currency) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.currency = currency;
    }

    public CreateOrderRequestDTO() {}

    public String getOrderItemId() {
        return orderItemId;
    }

    public String getProductId() {return productId;}

    public int getQuantity() {return quantity;}

    public double getAmount() {return amount;}

    public String getCurrency() {return currency;}


    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
