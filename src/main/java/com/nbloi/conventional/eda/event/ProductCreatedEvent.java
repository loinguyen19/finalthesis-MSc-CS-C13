package com.nbloi.conventional.eda.event;

import com.nbloi.conventional.eda.enums.EventType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreatedEvent {

    private String productId;
    private String name;
    private int stock;
    private BigDecimal price;
    private String currency;
    private String type;

    public ProductCreatedEvent(String productId, String name, BigDecimal price, int stock, String currency) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.currency = currency;
        this.type = EventType.PRODUCT_CREATED_EVENT.toString();
    }

    public ProductCreatedEvent() {}


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
