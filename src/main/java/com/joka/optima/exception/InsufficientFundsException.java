package com.joka.optima.exception;
/**
 * Исключение, выбрасываемое при недостаточности средств на карте.
 */
public class InsufficientFundsException extends RuntimeException{
    public InsufficientFundsException(String message) {
        super(message);
    }
}
