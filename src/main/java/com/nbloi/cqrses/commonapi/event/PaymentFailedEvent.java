package com.nbloi.cqrses.commonapi.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.PaymentStatus;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentFailedEvent {

    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private String type;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;
    private PaymentStatus paymentStatus;

    private String orderId;

    //TODO: add payment Status and payment method in this class

    public PaymentFailedEvent(String paymentId, String orderId, BigDecimal amount, String currency) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.type = EventType.PAYMENT_FAILED_EVENT.toString();
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public PaymentFailedEvent() {}

    public String getPaymentId() {return paymentId;}
    public BigDecimal getTotalAmount() {return amount;}
    public String getCurrency() {return currency;}
    public String getOrderId() {return orderId;}
    public String getType() {return type;}
    public LocalDateTime getPaymentDate() {return paymentDate;}
    public PaymentStatus getPaymentStatus() {return paymentStatus;}

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public void setTotalAmount(BigDecimal amount) {
        this.amount = amount;
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
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", type='" + type + '\'' +
                ", orderId='" + orderId + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
