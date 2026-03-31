package com.joka.optima.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Стандартный DTO ответа об ошибке.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
}
