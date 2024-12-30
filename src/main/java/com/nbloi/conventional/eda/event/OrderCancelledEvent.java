package com.nbloi.conventional.eda.event;

import com.nbloi.conventional.eda.enums.EventType;
import lombok.Getter;

import java.util.Objects;

@Getter
public class OrderCancelledEvent {
    private String orderId;
    private String type;

    public OrderCancelledEvent(String orderId) {
        this.orderId = orderId;
        this.type = EventType.ORDER_CANCELLED_EVENT.toString();
    }

    public OrderCancelledEvent() {}

    public String getOrderId() {return orderId;}

    public String getType() {return type;}

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setType(String type) {this.type = type;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderCancelledEvent that = (OrderCancelledEvent) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, type);
    }

    @Override
    public String toString() {
        return "OrderFailedToBePaidEvent{" +
                "orderId='" + orderId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
