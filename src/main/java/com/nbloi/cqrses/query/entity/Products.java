package com.nbloi.cqrses.query.entity;

import lombok.Builder;
import lombok.Data;
import javax.persistence.*;

@Data
@Builder
@Entity
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String productId;
    private String name;
    private double price;
    private int quantity;
//
//    @OneToOne(mappedBy = "products")
//    private OrderDetails orderDetails;

    public Products(String productId, String name, double price,  int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Products() {}

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {return quantity;}

    public double getPrice() {
        return price;
    }

//    public OrderDetails getOrderDetails() {
//        return orderDetails;
//    }


    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

//    public void setOrderDetails(OrderDetails orderDetails) {
//        this.orderDetails = orderDetails;
//    }

    @Override
    public String toString() {
        return "Products{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
//                ", orderDetails=" + orderDetails +
                '}';
    }
}
