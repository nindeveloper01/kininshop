package com.phoneshop.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String phoneModel, int requested, int available) {
        super("Insufficient stock for '%s': requested %d, available %d"
                .formatted(phoneModel, requested, available));
    }
}
