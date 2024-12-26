package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUID")
    @UuidGenerator
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String paymentId;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String paymentMethods;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Order order;

    //TODO: consider to add Customer into Payment entity

    public Payment() {
    }

    public Payment(String paymentId, BigDecimal totalAmount, PaymentStatus paymentStatus, String paymentMethods, LocalDateTime paymentDate, Order order) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethods = paymentMethods;
        this.paymentDate = paymentDate;
        this.order = order;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(String paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
}
