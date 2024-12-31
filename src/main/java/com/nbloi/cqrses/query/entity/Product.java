package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.cqrses.commonapi.enums.SystemDefault;
import lombok.*;
//import javax.persistence.*;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
@Builder
@Entity
public class Product {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUID")
//    @UuidGenerator
//    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String productId;

    private String name;
    private BigDecimal price;
    private int stock;
    private String currency;

//    @OneToMany(mappedBy = "product")
//    private Set<OrderItem> orderItems;

    public Product(String productId, String name, BigDecimal price, int stock, String currency) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.currency = currency;
//        this.orderItems = orderItems;
    }

    public Product() {}

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getStock() {return stock;}

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {return currency;}

//    public Set<OrderItem> getOrderItems() {return orderItems;}


    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCurrency(String currency) {this.currency = currency;}

//    public void setOrderItems(Set<OrderItem> orderItems) {
//        this.orderItems = orderItems;
//    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", currency='" + currency + '\'' +
//                ", orderItems=" + orderItems +
                '}';
    }
}
