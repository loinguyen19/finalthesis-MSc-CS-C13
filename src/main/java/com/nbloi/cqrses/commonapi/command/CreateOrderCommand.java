package com.nbloi.cqrses.commonapi.command;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.query.entity.OrderItem;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.List;

public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;
    private OrderStatus orderStatus;
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;

    // constructor, getters, equals/hashCode and toString
        public CreateOrderCommand(String orderId, List<OrderItem> orderItems, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.orderStatus = OrderStatus.CREATED;
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
    }


    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getOrderItems() {return orderItems;}

    public OrderStatus getOrderStatus() {return orderStatus;}

    public BigDecimal getTotalAmount() {return totalAmount;}

    public void setOrderItems(List<OrderItem> orderItems) {}

    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

}