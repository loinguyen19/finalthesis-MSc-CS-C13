package com.nbloi.cqrses.query.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

@Entity
public class ProductSalesView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUID")
    @UuidGenerator
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String productSalesViewId;
    private String productId;
    private String productName;
    private int totalQuantitySold;
    private double totalRevenue;


    public ProductSalesView() {
    }

    public ProductSalesView(String productId, String productName, int totalQuantitySold, double totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
    }

    public String getProductSalesViewId() {
        return productSalesViewId;
    }

    public void setProductSalesViewId(String productSalesViewId) {
        this.productSalesViewId = productSalesViewId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(int totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @Override
    public String toString() {
        return "ProductSalesView{" +
                "productSalesViewId='" + productSalesViewId + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", totalQuantitySold='" + totalQuantitySold + '\'' +
                ", totalRevenue='" + totalRevenue + '\'' +
                '}';
    }
}
