package com.nbloi.cqrses.query.entity;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table
public class OrderDetails {

    @Id
    private  String orderItemId;
    private OrderStatus orderStatus;
    private int quantity;

    @OneToOne
    @JoinColumn(name="product_id", nullable = false)
    private Products product;

    public OrderDetails(String orderItemId, int quantity) {
        this.orderItemId = orderItemId;
        orderStatus = OrderStatus.CREATED;
        this.quantity = quantity;
    }

    public OrderDetails() {}


    public String getOrderId() {return orderItemId;}

    public Products getProduct() {
        return product;
    }

    public String getOrderStatus() {return orderStatus.toString();}

    public int getQuantity() {
        return quantity;
    }


    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderConfirmed() {
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void setOrderShipped() {
        this.orderStatus = OrderStatus.SHIPPED;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "orderItemId='" + OrderDetails.this.orderItemId + '\'' +
                ", orderStatus=" + orderStatus +
                ", quantity=" + quantity +
//                ", product=" + product +
                '}';
    }
}

