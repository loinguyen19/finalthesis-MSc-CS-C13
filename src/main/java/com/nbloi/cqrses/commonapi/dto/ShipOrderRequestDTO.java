package com.nbloi.cqrses.commonapi.dto;

public class ShipOrderRequestDTO {

    private final String orderId;

    // constructor, getters, equals/hashCode and toString

    public ShipOrderRequestDTO(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {return orderId;}
}
