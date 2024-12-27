package com.nbloi.cqrses.commonapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderRequestDTO {

    private OrderStatus orderStatus;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalAmount;
    private String currency;
    private String customerId;
    private String paymentId;

    // constructor, getters, equals/hashCode and toString
    public CreateOrderRequestDTO( List<OrderItemDTO> orderItems, BigDecimal totalAmount, String customerId, String currency) {
        this.orderStatus = OrderStatus.CREATED;
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
//        this.paymentId = paymentId;
        this.currency = currency;
    }

    public CreateOrderRequestDTO() {}

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public OrderStatus getOrderStatus() {return this.orderStatus;}

    public BigDecimal getTotalAmount() {return this.totalAmount;}

    public String getCustomerId() {return this.customerId;}

    public String getPaymentId() {return this.paymentId;}

    public String getCurrency() {return this.currency;}

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public void setOrderStatus(OrderStatus orderStatus) {this.orderStatus = orderStatus;}

    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

    public void setCustomerId(String customerId) {this.customerId = customerId;}

    public void setPaymentId(String paymentId) {this.paymentId = paymentId;}

    public void setCurrency(String currency) {this.currency = currency;}

}
