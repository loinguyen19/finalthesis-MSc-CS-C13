package com.nbloi.cqrses.commonapi.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductDTO{

    private String productId;

    @NotBlank (message = "name can not be blank.")
    private String name;

    @NotNull (message = "price can not be blank.")
    private BigDecimal price;

    @NotNull (message = "stock can not be blank.")
    private int stock;

    @NotEmpty (message = "currency can not be blank.")
    private String currency = "VND";

    public ProductDTO(String name, int productQuantity, String productId, BigDecimal price, String currency) {
        this.name = name;
        this.stock = productQuantity;
        this.productId = productId;
        this.price = price;
        this.currency = currency;
    }

    public ProductDTO() {}

    public String getName() {return name;}
    public int getStock() {return stock;}
    public String getProductId() {return productId;}
    public BigDecimal getPrice() {return price;}
    public String getCurrency() {return currency;}

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

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productId='" + productId + '\'' +
                ", productName='" + name + '\'' +
                ", price=" + price +
                ", productStock=" + stock +
                ", currency='" + currency + '\'' +
                '}';
    }
}
