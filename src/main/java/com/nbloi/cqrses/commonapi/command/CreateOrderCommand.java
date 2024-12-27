package com.nbloi.cqrses.commonapi.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.query.entity.OrderItem;
import jakarta.persistence.Column;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;
    private String orderStatus;
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;
    private String customerId;
    private String paymentId;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private String currency;

    // constructor, getters, equals/hashCode and toString
        public CreateOrderCommand(String orderId, List<OrderItem> orderItems, BigDecimal totalAmount, String currency,
                                  String customerId, String paymentId) {
        this.orderId = orderId;
        this.orderStatus = OrderStatus.CREATED.toString();
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.createdAt = LocalDateTime.now();
        this.currency = currency;
    }


    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getOrderItems() {return orderItems;}

    public String getOrderStatus() {return orderStatus;}

    public BigDecimal getTotalAmount() {return totalAmount;}

    public String getCustomerId() {return customerId;}

    public String getPaymentId() {return paymentId;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public String getCurrency() {return currency;}

    public void setOrderItems(List<OrderItem> orderItems) {}

    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

    public void setCustomerId(String customerId) {this.customerId = customerId;}

    public void setPaymentId(String paymentId) {this.paymentId = paymentId;}

    protected void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public void setCurrency(String currency) {this.currency = currency;}

}