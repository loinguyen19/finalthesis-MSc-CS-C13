package com.nbloi.conventional.eda.dto;

import java.io.Serializable;

public class ShipOrderRequestDTO {

    private String orderId;

    // constructor, getters, equals/hashCode and toString

    public ShipOrderRequestDTO() {}

    public ShipOrderRequestDTO(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {return orderId;}
    public void setOrderId(String orderId) {this.orderId = orderId;}

}
