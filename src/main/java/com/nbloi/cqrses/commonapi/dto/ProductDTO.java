package com.nbloi.cqrses.commonapi.dto;

public class ProductDTO {

    private String productId;
    private String productName;
    private double price;
    private int productQuantity;

    public ProductDTO( String productName, int productQuantity, String productId, double price) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productId = productId;
        this.price = price;
    }

    public ProductDTO() {}

    public String getProductName() {return productName;}
    public int getProductQuantity() {return productQuantity;}
    public String getProductId() {return productId;}
    public double getPrice() {return price;}

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", productQuantity=" + productQuantity +
                '}';
    }
}
