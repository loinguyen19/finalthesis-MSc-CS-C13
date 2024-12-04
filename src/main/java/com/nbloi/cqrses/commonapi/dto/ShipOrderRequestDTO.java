package com.nbloi.cqrses.commonapi.dto;

public class ShipOrderRequestDTO {

    private String orderItemId;

    // constructor, getters, equals/hashCode and toString

    public ShipOrderRequestDTO() {}

    public ShipOrderRequestDTO(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getOrderId() {return orderItemId;}
}
