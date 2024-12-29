package com.nbloi.cqrses.commonapi.event;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentCreatedEvent {

    private String paymentId;
    private BigDecimal totalAmount;
    private String currency;
    private String type;

//    @Column(nullable = false, updatable = false)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private LocalDateTime paymentDate;
    private String paymentStatus;
    private String paymentMethods;

    public String givenList_shouldReturnARandomElement() {
        List<String> givenList = Arrays.asList("CASH", "DEBIT/CREDIT", "BNPL");
        Random rand = new Random();
        return givenList.get(rand.nextInt(givenList.size()));
    }

    private String orderId;


    public PaymentCreatedEvent(String paymentId, BigDecimal totalAmount, String currency, String orderId) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.paymentMethods = givenList_shouldReturnARandomElement();
        this.orderId = orderId;
        this.type = EventType.PAYMENT_CREATED_EVENT.toString();
        this.paymentStatus = PaymentStatus.CREATED.toString();
    }

    public PaymentCreatedEvent() {
        this.type = EventType.PAYMENT_CREATED_EVENT.toString();
        this.paymentStatus = PaymentStatus.CREATED.toString();
        this.paymentMethods = givenList_shouldReturnARandomElement();
    }

    public String getPaymentId() {return paymentId;}
    public BigDecimal getTotalAmount() {return totalAmount;}
    public String getCurrency() {return currency;}
    public String getOrderId() {return orderId;}
    public String getType() {return type;}
//    public LocalDateTime getPaymentDate() {return paymentDate;}
    public String getPaymentStatus() {return paymentStatus;}
    public String getPaymentMethods() {return paymentMethods;}

    public void setPaymentId(String paymentId) {this.paymentId = paymentId;}
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public void setType(String type) {this.type = type;}
//    public void setPaymentDate(LocalDateTime paymentDate) {}
    public void setPaymentStatus(String paymentStatus) {}
    public void setPaymentMethods(String paymentMethods) {}

    @Override
    public String toString() {
        return "PaymentEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", totalAmount=" + totalAmount +
                ", currency='" + currency + '\'' +
                ", type='" + type + '\'' +
                ", orderId='" + orderId + '\'' +
//                ", paymentDate=" + paymentDate +
                '}';
    }

//    @PrePersist
//    public void prePersist() {
//        paymentDate = LocalDateTime.now();
//    }

}
