package com.nbloi.cqrses.commonapi.event;

import lombok.Setter;

public class PaymentEvent {

    private String paymentId;
    private Double amount;

    public PaymentEvent(String paymentId, Double amount) {
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public String getPaymentId() {return paymentId;}
    public Double getAmount() {return amount;}


    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public void setAmount(Double amount) {}
}
