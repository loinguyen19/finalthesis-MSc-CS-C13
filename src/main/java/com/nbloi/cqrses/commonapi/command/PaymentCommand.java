package com.nbloi.cqrses.commonapi.command;


import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class PaymentCommand {

    @TargetAggregateIdentifier
    private String paymentId;
    private Double amount;
    private String currency;

    private String orderItemId;

    public PaymentCommand(String paymentId, Double amount, String currency, String orderId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.orderItemId = orderId;
    }

    public PaymentCommand() {}

    public String getPaymentId() {return paymentId;}
    public Double getAmount() {return amount;}
    public String getCurrency() {return currency;}
    public String getOrderItemId() {return orderItemId;}

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

}
