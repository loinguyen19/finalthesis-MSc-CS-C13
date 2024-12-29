package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class PaymentSummaryView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUID")
    @UuidGenerator
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String paymentSummaryId;
    private String orderId ;
    private String paymentId ;
    private String paymentStatus;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;

    private BigDecimal paymentTotalAmount;


    public PaymentSummaryView() {
    }

    public PaymentSummaryView(String paymentSummaryId, String orderId, String paymentId, String paymentStatus,
                              LocalDateTime paymentDate, BigDecimal paymentTotalAmount) {
        this.paymentSummaryId = paymentSummaryId;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.paymentTotalAmount = paymentTotalAmount;
    }

    public String getPaymentSummaryId() {
        return paymentSummaryId;
    }

    public void setPaymentSummaryId(String paymentSummaryId) {
        this.paymentSummaryId = paymentSummaryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    public void setPaymentTotalAmount(BigDecimal paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
    }

    @Override
    public String toString() {
        return "PaymentSummaryView{" +
                "paymentSummaryId='" + paymentSummaryId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", paymentDate=" + paymentDate +
                ", paymentTotalAmount=" + paymentTotalAmount +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }

}
