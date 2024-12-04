package com.nbloi.cqrses.commonapi.exception;

public class OutOfProductStockException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public OutOfProductStockException() {
        super("You can not buy quantity of products greater than stock. Please adjust the number!");
    }
}
