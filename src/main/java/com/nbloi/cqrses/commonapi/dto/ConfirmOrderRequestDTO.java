package com.nbloi.cqrses.commonapi.dto;

import java.io.Serializable;

public class ConfirmOrderRequestDTO{

    private String orderId;
    // constructor, getters, equals/hashCode and toString

    public ConfirmOrderRequestDTO() {}

    public ConfirmOrderRequestDTO(String orderId) {
        this.orderId = orderId;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {}

}
