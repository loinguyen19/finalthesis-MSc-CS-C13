package com.nbloi.cqrses.commonapi.event;

import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.query.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class OrderCreatedEvent {

    private String orderId;
    private OrderStatus orderStatus;
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;
    private String type;
    private String customerId;
    private String paymentId;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(String orderId, List<OrderItem> orderItems, OrderStatus orderStatus, BigDecimal totalAmount,
                             String customerId, String paymentId) {
        this.orderId = orderId;
        this.orderItems = orderItems;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.type = getClass().getSimpleName();
        this.customerId = customerId;
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public OrderStatus getOrderStatus() {return orderStatus;}

    public BigDecimal getTotalAmount() {return totalAmount;}

    public String getType() {return type;}

    public String getCustomerId() {return customerId;}

    public String getPaymentId() {return paymentId;}


    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setOrderStatus(OrderStatus orderStatus) {this.orderStatus = orderStatus;}

    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

    public void setType(String type) {this.type = type;}

    public void setCustomerId(String customerId) {this.customerId = customerId;}

    public void setPaymentId(String paymentId) {this.paymentId = paymentId;}


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return Objects.equals(orderId, that.orderId) && orderStatus == that.orderStatus &&
                Objects.equals(orderItems, that.orderItems) &&
                Objects.equals(totalAmount, that.totalAmount) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderStatus, orderItems, totalAmount, type);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId='" + orderId + '\'' +
                ", orderStatus=" + orderStatus +
                ", orderItems=" + orderItems +
                ", totalAmount=" + totalAmount +
                ", type='" + type + '\'' +
                '}';
    }
}
