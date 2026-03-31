package com.joka.optima.exception;

public class UnsupportedPaymentSystemException extends RuntimeException{
    public UnsupportedPaymentSystemException(String message) {
        super(message);
    }

    public UnsupportedPaymentSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
