package com.nbloi.conventional.eda.event;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nbloi.conventional.eda.enums.EventType;
import com.nbloi.conventional.eda.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
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

    private String orderId;


    public PaymentCreatedEvent(String paymentId, BigDecimal totalAmount, String currency, String orderId, String paymentMethods) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.orderId = orderId;
        this.type = EventType.PAYMENT_CREATED_EVENT.toString();
        this.paymentStatus = PaymentStatus.CREATED.toString();
        this.paymentMethods = paymentMethods;
    }

    public PaymentCreatedEvent() {
        this.type = EventType.PAYMENT_CREATED_EVENT.toString();
        this.paymentStatus = PaymentStatus.CREATED.toString();
    }

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

}
