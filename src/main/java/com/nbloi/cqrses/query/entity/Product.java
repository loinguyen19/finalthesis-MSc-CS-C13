package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    private String productId;

    @NotBlank(message = "name can not be blank.")
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull(message = "price can not be blank.")
    private BigDecimal price;

    @NotNull (message = "stock can not be blank.")
    private int stock;

    @NotEmpty(message = "currency can not be blank.")
    private String currency = "VND";

    @NotEmpty (message = "product status can not be blank.")
    private String productStatus;

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private Set<OrderItem> orderItems;

    public Product(String productId, String name, BigDecimal price, int stock, String currency, String productStatus) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.currency = currency;
//        this.orderItems = orderItems;
        this.productStatus = productStatus;
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

    public String getProductStatus() {return productStatus;}

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

    public void setProductStatus(String productStatus) {this.productStatus = productStatus;}

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
                ", productStatus='" + productStatus + '\'' +
//                ", orderItems=" + orderItems +
                '}';
    }
}
