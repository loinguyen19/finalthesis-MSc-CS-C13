package com.nbloi.cqrses.commonapi.dto;

public class CreateOrderRequestDTO {

    private final String orderId;
    private final String productId;

    // constructor, getters, equals/hashCode and toString
    public CreateOrderRequestDTO(String orderId, String productId) {
        this.orderId = orderId;
        this.productId = productId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {return productId;}
}
