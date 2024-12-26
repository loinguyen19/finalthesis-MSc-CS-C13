package com.nbloi.cqrses.query.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
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
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {

    @Id
    @JsonProperty("paymentId")
    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUID")
    @UuidGenerator
    private String paymentId;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String paymentMethods;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("currency")
    private String currency;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    @JsonBackReference
    private Order order;

    // TODO: Message persisted in Outbox Message table: []
    //Received Payment Event: {"orderId":"b7ef3725-7757-480a-9470-9d7d1e698035","orderStatus":"CREATED","orderItems":[{"orderItemId":"UUID-12345678912345678999","quantity":6,"price":30.00,"totalPrice":500,"currency":"VND","product":{"productId":"UUID-10","name":"Towel","price":30.00,"stock":2218,"currency":"VND"}},{"orderItemId":"UUID-12345678912345689109","quantity":2,"price":1500.00,"totalPrice":3000,"currency":"VND","product":{"productId":"UUID-2","name":"MotorBike","price":1500.00,"stock":89000,"currency":"VND"}}],"totalAmount":3500,"customerId":"UUID-C-6","paymentId":"7b74322c-567f-49fc-a78e-8530e40ab4f4","currency":"VND","type":"OrderCreatedEvent"}
    //com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "orderItems" (class com.nbloi.cqrses.query.entity.Payment), not marked as ignorable (9 known properties: "orderStatus", "paymentId", "paymentDate", "paymentStatus", "orderId", "totalAmount", "order", "currency", "paymentMethods"])
    // at [Source: (String)"{"orderId":"b7ef3725-7757-480a-9470-9d7d1e698035","orderStatus":"CREATED","orderItems":[{"orderItemId":"UUID-12345678912345678999","quantity":6,"price":30.00,"totalPrice":500,"currency":"VND","product":{"productId":"UUID-10","name":"Towel","price":30.00,"stock":2218,"currency":"VND"}},{"orderItemId":"UUID-12345678912345689109","quantity":2,"price":1500.00,"totalPrice":3000,"currency":"VND","product":{"productId":"UUID-2","name":"MotorBike","price":1500.00,"stock":89000,"currency":"VND"}}],"total"[truncated 132 chars]; line: 1, column: 89] (through reference chain: com.nbloi.cqrses.query.entity.Payment["orderItems"])
    //	at com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.from(UnrecognizedPropertyException.java:61)

    //TODO: consider to add Customer into Payment entity

    public Payment() {
    }

    public Payment(String paymentId, BigDecimal totalAmount, PaymentStatus paymentStatus, String paymentMethods,
                   LocalDateTime paymentDate, String orderId, String currency, Order order) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethods = paymentMethods;
        this.paymentDate = paymentDate;
        this.orderId = order.getOrderId();
//        this.orderStatus = orderStatus;
        this.currency = order.getCurrency();
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

    public String getOrderId() {return orderId;}

    public void setOrderId(String orderId) {this.orderId = orderId;}

//    public OrderStatus getOrderStatus() {return orderStatus;}
//
//    public void setOrderStatus(OrderStatus orderStatus) {this.orderStatus = orderStatus;}

    public String getCurrency() {return currency;}

    public void setCurrency(String currency) {this.currency = currency;}

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
}
