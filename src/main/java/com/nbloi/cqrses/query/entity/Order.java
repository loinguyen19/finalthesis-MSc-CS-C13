package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="Orders")
public class Order {

    @Id
    private String orderId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private String currency;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;


    @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name="payment_id",nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonManagedReference
    private Payment payment;

    public Order() {}

    public Order(String orderId, OrderStatus orderStatus,BigDecimal totalAmount, LocalDateTime createdAt,
                 LocalDateTime updatedAt, Set<OrderItem> orderItems) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
//        this.customer = customer;
        this.orderItems = orderItems;
        this.currency = "VND";
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Customer getCustomer() {return customer;}

    public Payment getPayment() {
        return payment;
    }

    public String getCurrency() {return currency;}

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderCreatedStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderConfirmedStatus() {
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void setOrderShippedStatus() {
        this.orderStatus = OrderStatus.SHIPPED;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setCustomer(Customer customer) {this.customer = customer;}

    public void setPayment(Payment payment) {this.payment = payment;}

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCurrency(String currency) {this.currency = currency;}


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
