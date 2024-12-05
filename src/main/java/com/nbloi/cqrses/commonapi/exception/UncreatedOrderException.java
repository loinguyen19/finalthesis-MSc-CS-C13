package com.nbloi.cqrses.commonapi.exception;

public class UncreatedOrderException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UncreatedOrderException() {
        super("Order has not been created succesfully. Please try again.");
    }
}
