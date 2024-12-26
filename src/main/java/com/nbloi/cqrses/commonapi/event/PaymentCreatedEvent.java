package com.nbloi.cqrses.commonapi.event;


import com.nbloi.cqrses.commonapi.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentCreatedEvent {

    private String paymentId;
    private BigDecimal totalAmount;
    private String currency;
    private String type;
    private LocalDateTime paymentDate;
    private PaymentStatus paymentStatus;

    private String orderId;


    public PaymentCreatedEvent(String paymentId, BigDecimal totalAmount, String currency, String orderId) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.orderId = orderId;
        this.type = this.getClass().getSimpleName();
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.CREATED;
    }

    public PaymentCreatedEvent() {}

    public String getPaymentId() {return paymentId;}
    public BigDecimal getTotalAmount() {return totalAmount;}
    public String getCurrency() {return currency;}
    public String getOrderId() {return orderId;}
    public String getType() {return type;}
    public LocalDateTime getPaymentDate() {return paymentDate;}
    public PaymentStatus getPaymentStatus() {return paymentStatus;}

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
    public void setPaymentDate(LocalDateTime paymentDate) {}
    public void setPaymentStatus(PaymentStatus paymentStatus) {}

    @Override
    public String toString() {
        return "PaymentEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", totalAmount=" + totalAmount +
                ", currency='" + currency + '\'' +
                ", type='" + type + '\'' +
                ", orderId='" + orderId + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }

}
