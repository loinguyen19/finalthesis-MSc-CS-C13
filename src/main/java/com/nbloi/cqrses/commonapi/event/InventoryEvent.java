package com.nbloi.cqrses.commonapi.event;

public class InventoryEvent {

    private String inventoryId;
    private String productName;
    private int productQuantity;

    public InventoryEvent(String inventoryId, String productName, int productQuantity) {
        this.inventoryId = inventoryId;
        this.productName = productName;
        this.productQuantity = productQuantity;
    }

    public String getInventoryId() {return inventoryId;}
    public String getProductName() {return productName;}
    public int getProductQuantity() {return productQuantity;}

    @Override
    public String toString() {
        return "InventoryEvent{" +
                "inventoryId='" + inventoryId + '\'' +
                ", productName='" + productName + '\'' +
                ", productQuantity=" + productQuantity +
                '}';
    }
}
