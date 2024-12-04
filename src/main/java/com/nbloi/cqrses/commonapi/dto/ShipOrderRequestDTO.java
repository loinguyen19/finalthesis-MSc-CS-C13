package com.nbloi.cqrses.commonapi.dto;

public class ShipOrderRequestDTO {

    private String orderId;

    // constructor, getters, equals/hashCode and toString

    public ShipOrderRequestDTO() {}

    public ShipOrderRequestDTO(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {return orderId;}
}
