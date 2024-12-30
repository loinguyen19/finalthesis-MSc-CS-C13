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
public class PaymentFailedEvent {

    private String paymentId;
    private BigDecimal totalAmount;
    private String currency;
    private String type;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;
    private String paymentStatus;
    private String paymentMethods;

    private String orderId;

    //TODO: add payment Status and payment method in this class

    public PaymentFailedEvent(String paymentId, String orderId, BigDecimal totalAmount, String currency, String paymentMethods) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.orderId = orderId;
        this.type = EventType.PAYMENT_FAILED_EVENT.toString();
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.FAILED.toString();
        this.paymentMethods = paymentMethods;
    }

    public PaymentFailedEvent() {}

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
