package com.nbloi.cqrses.commonapi.event;

import com.nbloi.cqrses.commonapi.enums.EventType;

import java.math.BigDecimal;

public class ProductInventoryEvent {

    private String productId;
    private String name;
    private int stock;
    private BigDecimal price;
    private String currency;

    private String type;

    public ProductInventoryEvent(String productId, String name, int stock, BigDecimal price, String currency) {
        this.productId = productId;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.currency = currency;
        this.type = EventType.PRODUCT_INVENTORY_UPDATED_EVENT.toString();
    }

    public ProductInventoryEvent() {}

    public String getProductId() {return productId;}
    public String getName() {return name;}
    public int getStock() {return stock;}
    public BigDecimal getPrice() {return price;}
    public String getCurrency() {return currency;}
    public String getType() {return type;}


    public void setProductId(String productId) {
        this.productId = productId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setPrice(BigDecimal price) {this.price = price;}
    public void setCurrency(String currency) {this.currency = currency;}
    public void setType(String type) {this.type = type;}

    @Override
    public String toString() {
        return "ProductInventoryEvent{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
