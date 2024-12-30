package com.nbloi.conventional.eda.event;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.nbloi.conventional.eda.enums.EventType;
import com.nbloi.conventional.eda.enums.PaymentStatus;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentCompletedEvent {

    private String paymentId;
    private BigDecimal totalAmount;
    private String currency;
    private String type;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;
    private String paymentStatus;
    private String paymentMethods;

    private String orderId;


    public PaymentCompletedEvent(String paymentId, BigDecimal totalAmount, String currency, String orderId, String paymentMethods) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.orderId = orderId;
        this.type = EventType.PAYMENT_COMPLETED_EVENT.toString();
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.CREATED.toString();
        this.paymentMethods = paymentMethods;
    }

    public PaymentCompletedEvent() {}

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
