package com.nbloi.cqrses.commonapi.event;

import java.time.LocalDateTime;

public class CustomerProductRatedEvent {

    private String productRatedId;
    private double rating;
    private LocalDateTime ratedAt;

    private String customerId;
    private String productId;

    public CustomerProductRatedEvent() {
    }

    public CustomerProductRatedEvent(String customerId, String productId, double rating, LocalDateTime ratedAt) {
        this.customerId = customerId;
        this.productId = productId;
        this.rating = rating;
        this.ratedAt = ratedAt;
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
