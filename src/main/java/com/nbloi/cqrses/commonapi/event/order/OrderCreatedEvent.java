package com.nbloi.cqrses.commonapi.event.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.query.entity.OrderItem;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class OrderCreatedEvent {

    private String orderId;
    private String orderStatus;
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;
    private String customerId;
    private String paymentId;
    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private String type;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(String orderId, List<OrderItem> orderItems, String orderStatus, BigDecimal totalAmount,
                             String currency, String customerId, String paymentId) {
        this.orderId = orderId;
        this.orderItems = orderItems;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.customerId = customerId;
        this.paymentId = paymentId;
//        this.createdAt = LocalDateTime.now();
        this.type = EventType.ORDER_CREATED_EVENT.toString();
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public String getOrderStatus() {return orderStatus;}

    public BigDecimal getTotalAmount() {return totalAmount;}

    public String getCustomerId() {return customerId;}

    public String getPaymentId() {return paymentId;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public String getCurrency() {return currency;}

    public String getType() {return type;}

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setOrderStatus(String orderStatus) {this.orderStatus = orderStatus;}

    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

    public void setCustomerId(String customerId) {this.customerId = customerId;}

    public void setPaymentId(String paymentId) {this.paymentId = paymentId;}

    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public void setCurrency(String currency) {this.currency = currency;}

    public void setType(String type) {this.type = type;}


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return Objects.equals(orderId, that.orderId) && orderStatus == that.orderStatus
                && Objects.equals(orderItems, that.orderItems) && Objects.equals(totalAmount, that.totalAmount)
                && Objects.equals(customerId, that.customerId)
//                && Objects.equals(paymentId, that.paymentId)
                && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderStatus, orderItems, totalAmount, customerId, currency);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId='" + orderId + '\'' +
                ", orderStatus=" + orderStatus +
                ", orderItems=" + orderItems +
                ", totalAmount=" + totalAmount +
                ", customerId='" + customerId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
