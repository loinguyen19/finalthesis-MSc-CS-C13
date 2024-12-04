//package com.nbloi.cqrses.query.entity;
//
//import lombok.Builder;
//import lombok.Data;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//
//@Data
//@Builder
//@Entity
//@Table(name="inventory_detail")
//public class InventoryDetails {
//
//    @Id
//    private String inventoryId;
//    private String description;
//
//    @OneToOne(mappedBy = "inventory_detail")
//    private Products product;
//
//    private int quantity;
//
//
//    public InventoryDetails(String inventoryId, String description, Products product, int quantity) {
//        this.inventoryId = inventoryId;
//        this.description = description;
//        this.quantity = quantity;
//        this.product = product;
//    }
//
//    public InventoryDetails() {}
//
//    public String getInventoryId() {
//        return inventoryId;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public Products getProduct() {
//        return product;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    @Override
//    public String toString() {
//        return "InventoryDetails{" +
//                "inventoryId='" + inventoryId + '\'' +
//                ", description='" + description + '\'' +
//                ", product='" + product + '\'' +
//                ", quantity=" + quantity +
//                '}';
//    }
//}
