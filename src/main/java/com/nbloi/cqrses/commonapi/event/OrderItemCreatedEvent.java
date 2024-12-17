//package com.nbloi.cqrses.commonapi.event;
//
//import java.math.BigDecimal;
//
//public class OrderItemCreatedEvent {
//
//    private String orderItemId;
//    private int quantity;
//    private BigDecimal price;
//    private BigDecimal totalPrice;
//    private String currency;
//
//    private String productId;
//
//    public OrderItemCreatedEvent(String orderItemId, int quantity, BigDecimal price, BigDecimal totalPrice, String currency,
//                                 String productId) {
//        this.orderItemId = orderItemId;
//        this.quantity = quantity;
//        this.price = price;
//        this.totalPrice = totalPrice;
//        this.currency = currency;
//        this.productId = productId;
//    }
//
//    public OrderItemCreatedEvent() {
//    }
//
//    public String getOrderItemId() {
//        return orderItemId;
//    }
//
//    public void setOrderItemId(String orderItemId) {
//        this.orderItemId = orderItemId;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//
//    public BigDecimal getPrice() {
//        return price;
//    }
//
//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }
//
//    public BigDecimal getTotalPrice() {
//        return totalPrice;
//    }
//
//    public void setTotalPrice(BigDecimal totalPrice) {
//        this.totalPrice = totalPrice;
//    }
//
//    public String getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(String currency) {
//        this.currency = currency;
//    }
//
//
//    public String getProductId() {
//        return productId;
//    }
//
//    public void setProductId(String productId) {
//        this.productId = productId;
//    }
//
//
//    @Override
//    public String toString() {
//        return "OrderItemCreatedEvent{" +
//                "orderItemId='" + orderItemId + '\'' +
//                ", quantity=" + quantity +
//                ", price=" + price +
//                ", totalPrice=" + totalPrice +
//                ", currency='" + currency + '\'' +
//                ", productId='" + productId + '\'' +
//                '}';
//    }
//}
