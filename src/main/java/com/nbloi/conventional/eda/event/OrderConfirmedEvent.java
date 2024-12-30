package com.nbloi.conventional.eda.event;

import com.nbloi.conventional.eda.enums.EventType;
import lombok.Getter;

import java.util.Objects;

@Getter
public class OrderConfirmedEvent {

    private String orderId;
    private String type;

    public OrderConfirmedEvent(String orderId) {
        this.orderId = orderId;
        this.type = EventType.ORDER_CONFIRMED_EVENT.toString();
    }

    public OrderConfirmedEvent() {}

    public String getOrderId() {return orderId;}

    public String getType() {return type;}

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setType(String type) {this.type = type;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderConfirmedEvent that = (OrderConfirmedEvent) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, type);
    }

    @Override
    public String toString() {
        return "OrderConfirmedEvent{" +
                "orderId='" + orderId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}