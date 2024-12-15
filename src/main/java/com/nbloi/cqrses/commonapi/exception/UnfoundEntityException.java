package com.nbloi.cqrses.commonapi.exception;

import java.io.Serial;

public class UnfoundEntityException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UnfoundEntityException(String orderId, String entityType) {
        super(String.format("%s entity with id: '%s' not found. Please check the order id again", entityType, orderId));
    }
}
