package com.nbloi.cqrses.commonapi.dto;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.query.entity.Product;

public class OrderDetailsDTO {

    private  String orderItemId;
    private OrderStatus orderStatus;
    private int quantity;
    private double amount;
    private String currency;

    private Product product;

    public OrderDetailsDTO(String orderItemId, OrderStatus orderStatus,
                           int quantity, double amount, String currency, Product product) {
        this.orderItemId = orderItemId;
        this.orderStatus = orderStatus;
        this.quantity = quantity;
        this.amount = amount;
        this.currency = currency;
        this.product = product;
    }

    public OrderDetailsDTO() {}


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

