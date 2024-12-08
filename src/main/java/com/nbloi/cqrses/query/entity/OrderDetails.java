package com.nbloi.cqrses.query.entity;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
//import javax.persistence.Entity;
//import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;

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
    private double amount;
    private String currency;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="product_id")
    private Product product;

    public OrderDetails(String orderItemId, int quantity, double amount, String currency, Product product) {
        this.orderItemId = orderItemId;
        orderStatus = OrderStatus.CREATED;
        this.quantity = quantity;
        this.amount = amount;
        this.currency = currency;
        this.product = product;
    }

    public OrderDetails() {}


    public String getOrderId() {return orderItemId;}

    public Product getProduct() {
        return product;
    }

    public String getOrderStatus() {return orderStatus.toString();}

    public int getQuantity() {
        return quantity;
    }

    public double getAmount() {return amount;}

    public String getCurrency() {return currency;}


    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setProduct(Product product) {
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

    public void setAmount(double amount) {this.amount = amount;}

    public void setCurrency(String currency) {this.currency = currency;}

    @Override
    public String toString() {
        return "OrderDetails{" +
                "orderItemId='" + orderItemId + '\'' +
                ", orderStatus=" + orderStatus +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", product=" + product +
                '}';
    }
}

