package com.nbloi.cqrses.commonapi.event;



public class PaymentEvent {

    private String paymentId;
    private Double amount;

    public PaymentEvent(String paymentId, Double amount) {
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public PaymentEvent() {}

    public String getPaymentId() {return paymentId;}
    public Double getAmount() {return amount;}


    public void setAmount(Double amount) {}
}
