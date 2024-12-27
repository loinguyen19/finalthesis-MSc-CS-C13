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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Entity
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {

    @Id
    @JsonProperty("paymentId")
    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUID")
    @UuidGenerator
    private String paymentId;
    private BigDecimal totalAmount;
    private String paymentStatus;
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

    //TODO: consider to add Customer into Payment entity
    public String givenList_shouldReturnARandomElement() {
        List<String> givenList = Arrays.asList("CASH", "DEBIT/CREDIT", "BNPL");
        Random rand = new Random();
        return givenList.get(rand.nextInt(givenList.size()));
    }

    public Payment() {
    }

    public Payment(String paymentId, BigDecimal totalAmount, String paymentStatus, String paymentMethods,
                   LocalDateTime paymentDate, String orderId, String currency, Order order) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethods = givenList_shouldReturnARandomElement();
        this.paymentDate = paymentDate;
        this.orderId = order.getOrderId();
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
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
