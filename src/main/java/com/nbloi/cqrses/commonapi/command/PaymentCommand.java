package com.nbloi.cqrses.commonapi.command;


import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public class PaymentCommand {

    @TargetAggregateIdentifier
    private String paymentId;
    private BigDecimal amount;
    private String currency;

    private String orderId;

    public PaymentCommand(String paymentId, BigDecimal amount, String currency, String orderId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
    }

    public PaymentCommand() {}

    public String getPaymentId() {return paymentId;}
    public BigDecimal getAmount() {return amount;}
    public String getCurrency() {return currency;}
    public String getOrderItemId() {return orderId;}

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setOrderItemId(String orderId) {
        this.orderId = orderId;
    }

}
