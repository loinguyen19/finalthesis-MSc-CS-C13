package com.nbloi.cqrses.query.entity;

import lombok.Builder;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@Entity
public class InventoryDetails {

    @Id
    private String inventoryId;
    private String description;

    private String productId;
    private int quantity;


    public InventoryDetails(String inventoryId, String description, String productId, int quantity) {
        this.inventoryId = inventoryId;
        this.description = description;
        this.productId = productId;
        this.quantity = quantity;
    }

    public InventoryDetails() {}

    public String getInventoryId() {
        return inventoryId;
    }

    public String getDescription() {
        return description;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "InventoryDetails{" +
                "inventoryId='" + inventoryId + '\'' +
                ", description='" + description + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
