package com.nbloi.cqrses.commonapi.dto;

public class ConfirmOrderRequestDTO {

    private final String orderId;

    // constructor, getters, equals/hashCode and toString

    public ConfirmOrderRequestDTO(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
