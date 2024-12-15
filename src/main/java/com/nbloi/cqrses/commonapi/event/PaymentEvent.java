package com.nbloi.cqrses.commonapi.event;


import com.nbloi.cqrses.commonapi.enums.EventType;

import java.math.BigDecimal;

public class PaymentEvent {

    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private String type;

    //TODO: add relationship mapping with Order
    private String orderId;

    //TODO: add payment Status and payment method in this class

    public PaymentEvent(String paymentId, BigDecimal amount, String currency, String orderId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.type = EventType.PAYMENT_EVENT.toString();
    }

    public PaymentEvent() {}

    public String getPaymentId() {return paymentId;}
    public BigDecimal getAmount() {return amount;}
    public String getCurrency() {return currency;}
    public String getOrderId() {return orderId;}
    public String getType() {return type;}

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public void setType(String type) {this.type = type;}

    @Override
    public String toString() {
        return "PaymentEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", type='" + type + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }

}
