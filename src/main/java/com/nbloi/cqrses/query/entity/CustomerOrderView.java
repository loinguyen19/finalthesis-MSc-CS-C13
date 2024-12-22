package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class CustomerOrderView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUID")
    @UuidGenerator
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String customerOrderViewId;
    private String customerId;
    private String customerName;
    private String orderId;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    private String orderStatus;
    private BigDecimal totalOrderAmount;

    public CustomerOrderView() {
    }

    public CustomerOrderView(String customerId, String customerName, String orderId,
                             String orderDate, String orderStatus, BigDecimal totalOrderAmount) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.orderId = orderId;
        this.orderDate = LocalDateTime.parse(orderDate);
        this.orderStatus = orderStatus;
        this.totalOrderAmount = totalOrderAmount;
    }

    public String getCustomerOrderViewId() {
        return customerOrderViewId;
    }

    public void setCustomerOrderViewId(String customerOrderViewId) {
        this.customerOrderViewId = customerOrderViewId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }


    @Override
    public String toString() {
        return "CustomerOrderView{" +
                "customerOrderViewId=" + customerOrderViewId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", totalOrderAmount=" + totalOrderAmount +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }

}
