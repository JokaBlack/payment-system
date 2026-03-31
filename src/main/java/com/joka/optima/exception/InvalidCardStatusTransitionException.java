package com.joka.optima.exception;

public class InvalidCardStatusTransitionException extends RuntimeException{
    public InvalidCardStatusTransitionException(String message) {
        super(message);
    }
}
