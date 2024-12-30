package com.nbloi.conventional.eda.exception;

public class UnconfirmedOrderException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnconfirmedOrderException() {
        super("The order has not been confirmed. Please re-check and try again.");
    }
}
