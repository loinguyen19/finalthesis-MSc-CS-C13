package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
//import javax.persistence.Entity;
//import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItem {

    @Id
    private  String orderItemId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String currency;


    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Order order;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="product_id", nullable = false)
    private Product product;


    public OrderItem(String orderItemId, int quantity, BigDecimal price, String currency, Product product) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        this.currency = currency;
        this.product = product;
//        this.order = order;
    }

    public OrderItem() {}


    public String getOrderItemId() {return orderItemId;}

    public Product getProduct() {
        return product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {return price;}

    public String getCurrency() {return currency;}

    public BigDecimal getTotalPrice() {return totalPrice;}

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setPrice(BigDecimal price) {this.price = price;}

    public void setCurrency(String currency) {this.currency = currency;}

    public void setTotalPrice(BigDecimal totalPrice) {this.totalPrice = totalPrice;}


    @Override
    public String toString() {
        return "OrderDetails{" +
                "orderItemId='" + orderItemId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", product=" + product +
                '}';
    }
}

