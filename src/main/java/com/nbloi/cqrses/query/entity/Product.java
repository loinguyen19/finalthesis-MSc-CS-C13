package com.nbloi.cqrses.query.entity;

import lombok.Builder;
import lombok.Data;
import javax.persistence.*;

@Data
@Builder
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String productId;

    private String name;
    private double price;
    private int stock;
    private String currency;

//    @OneToOne(fetch = FetchType.LAZY, mappedBy = "product")
//    private OrderDetails orderDetails;

    public Product(String productId, String name, double price, int stock, String currency) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.currency = currency;
//        this.orderDetails = orderDetails;
    }

    public Product() {}

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getStock() {return stock;}

    public double getPrice() {
        return price;
    }

    public String getCurrency() {return currency;}


    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCurrency(String currency) {this.currency = currency;}

    @Override
    public String toString() {
        return "Products{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", currency='" + currency + '\'' +
                '}';
    }
}
