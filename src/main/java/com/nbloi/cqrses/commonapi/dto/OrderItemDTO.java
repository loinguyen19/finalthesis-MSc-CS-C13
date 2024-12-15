package com.nbloi.cqrses.commonapi.dto;

import com.nbloi.cqrses.query.entity.Product;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDTO {

    private String orderItemId;
    private String productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String currency;


    public OrderItemDTO(String orderItemId, String productId, int quantity, BigDecimal price,String currency) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        this.currency = currency;
    }

    public OrderItemDTO() {}


    public String getOrderItemId() {return orderItemId;}

//    public String getOrderId() {return orderId;}
    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getCurrency() {
        return currency;
    }


    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "orderItemId='" + orderItemId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalPrice=" + totalPrice +
                ", currency='" + currency + '\'' +
                '}';
    }
}

