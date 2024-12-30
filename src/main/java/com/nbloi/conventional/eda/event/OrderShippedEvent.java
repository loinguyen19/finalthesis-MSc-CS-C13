package com.nbloi.conventional.eda.event;

import com.nbloi.conventional.eda.enums.EventType;
import lombok.Getter;

import java.util.Objects;

@Getter
public class OrderShippedEvent {

    private String orderId;
    private String type;

    // default constructor, getters, equals/hashCode and toString


    public OrderShippedEvent(String orderId) {
        this.orderId = orderId;
        this.type = EventType.ORDER_SHIPPED_EVENT.toString();
    }

    public OrderShippedEvent() {}

    public String getOrderId() {
        return orderId;
    }

    public String getType() {return this.type;}

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setType(String type) {this.type = type;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderShippedEvent that = (OrderShippedEvent) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(type, that.type) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, type);
    }
}