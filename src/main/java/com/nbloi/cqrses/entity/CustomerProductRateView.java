package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;

@Entity
public class CustomerProductRateView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUID")
    @UuidGenerator
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String customerProductRateId;
    private String customerId;
    private String productId;
    private double rating;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ratedAt;

    public CustomerProductRateView() {
    }

    public CustomerProductRateView(String customerId, String productId, double rating, LocalDateTime ratedAt) {
        this.customerId = customerId;
        this.productId = productId;
        this.rating = rating;
        this.ratedAt = ratedAt;
    }

    public String getCustomerProductRateId() {
        return customerProductRateId;
    }

    public void setCustomerProductRateId(String customerProductRateId) {
        this.customerProductRateId = customerProductRateId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    @Override
    public String toString() {
        return "CustomerProductRateView{" +
                "customerProductRateId='" + customerProductRateId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", productId='" + productId + '\'' +
                ", rating=" + rating +
                ", ratedAt=" + ratedAt +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        ratedAt = LocalDateTime.now();
    }

}
