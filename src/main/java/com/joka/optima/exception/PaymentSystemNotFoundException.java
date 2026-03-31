package com.joka.optima.exception;

public class PaymentSystemNotFoundException extends RuntimeException{
    public PaymentSystemNotFoundException(String message) {
        super(message);
    }
}
