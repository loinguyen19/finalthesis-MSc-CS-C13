package com.nbloi.cqrses.commonapi.dto;

public class ConfirmOrderRequestDTO {

    private String orderItemId;
    // constructor, getters, equals/hashCode and toString

    public ConfirmOrderRequestDTO() {}

    public ConfirmOrderRequestDTO(String orderItemId) {
        this.orderItemId = orderItemId;
    }


    public String getOrderId() {
        return orderItemId;
    }

    public void setOrderId(String orderItemId) {}

}
