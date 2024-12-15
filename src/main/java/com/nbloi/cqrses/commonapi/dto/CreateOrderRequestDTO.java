package com.nbloi.cqrses.commonapi.dto;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.query.entity.OrderItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequestDTO {

    private OrderStatus orderStatus;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalAmount;

    // constructor, getters, equals/hashCode and toString
    public CreateOrderRequestDTO( List<OrderItemDTO> orderItems) {
        this.orderStatus = OrderStatus.CREATED;
        this.orderItems = orderItems;
    }

    public CreateOrderRequestDTO() {}

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public OrderStatus getOrderStatus() {return this.orderStatus;}

    public BigDecimal getTotalAmount() {return this.totalAmount;}

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public void setOrderStatus(OrderStatus orderStatus) {this.orderStatus = orderStatus;}

    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

}
