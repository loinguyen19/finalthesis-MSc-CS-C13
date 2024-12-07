package com.nbloi.cqrses.commonapi.dto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ProductDTO {

    private String productId;

    @NotBlank (message = "name can not be blank.")
    private String name;

    @NotNull (message = "price can not be blank.")
    private double price;

    @NotNull (message = "stock can not be blank.")
    private int stock;

    @NotEmpty (message = "currency can not be blank.")
    private String currency = "VND";

    public ProductDTO(String name, int productQuantity, String productId, double price, String currency) {
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
    public double getPrice() {return price;}
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
