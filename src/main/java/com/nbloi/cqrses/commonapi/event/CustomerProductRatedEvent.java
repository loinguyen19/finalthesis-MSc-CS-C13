package com.nbloi.cqrses.commonapi.event;

import java.time.LocalDateTime;

public class CustomerProductRatedEvent {

    private String productRatedId;
    private double rating;
    private LocalDateTime ratedAt;

    private String productId;
    private String customerId;

    public CustomerProductRatedEvent() {
    }

    public CustomerProductRatedEvent(double rating, LocalDateTime ratedAt, String productId, String customerId) {
        this.rating = rating;
        this.ratedAt = ratedAt;
        this.productId = productId;
        this.customerId = customerId;
    }

    public String getProductRatedId() {
        return productRatedId;
    }

    public void setProductRatedId(String productRatedId) {
        this.productRatedId = productRatedId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public LocalDateTime getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(LocalDateTime ratedAt) {
        this.ratedAt = ratedAt;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "ProductRatedEvent{" +
                "productRatedId='" + productRatedId + '\'' +
                ", rating=" + rating +
                ", ratedAt=" + ratedAt +
                ", productId='" + productId + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
