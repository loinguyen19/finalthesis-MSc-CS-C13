package com.nbloi.cqrses.commonapi.event;

public class ProductInventoryEvent {

    private String productId;
    private String name;
    private int stock;
    private double price;
    private String currency;

    public ProductInventoryEvent(String productId, String name, int stock, double price, String currency) {
        this.productId = productId;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.currency = currency;
    }

    public ProductInventoryEvent() {}

    public String getProductId() {return productId;}
    public String getName() {return name;}
    public int getStock() {return stock;}
    public double getPrice() {return price;}
    public String getCurrency() {return currency;}


    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrice(double price) {this.price = price;}

    public void setCurrency(String currency) {this.currency = currency;}

    @Override
    public String toString() {
        return "ProductInventoryEvent{" +
                "productId='" + productId + '\'' +
                ", productName='" + name + '\'' +
                ", productStock=" + stock +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                '}';
    }
}
