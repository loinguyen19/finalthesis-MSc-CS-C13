package com.nbloi.cqrses.commonapi.event;

public class ProductInventoryEvent {

    private String productId;
    private String productName;
    private int productQuantity;
    private double price;

    public ProductInventoryEvent(String productName, int productQuantity, String productId, double price) {
        this.productId = productId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.price = price;
    }

    public ProductInventoryEvent() {}

    public String getProductId() {return productId;}
    public String getProductName() {return productName;}
    public int getProductQuantity() {return productQuantity;}
    public double getPrice() {return price;}


    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public void setPrice(double price) {this.price = price;}

    @Override
    public String toString() {
        return "ProductInventoryEvent{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productQuantity=" + productQuantity +
                ", price=" + price +
                '}';
    }
}
