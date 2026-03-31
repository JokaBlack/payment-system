package com.joka.optima.exception;

import com.joka.optima.dto.response.ErrorResponse;
import com.joka.optima.enums.PaymentSystemCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений приложения.
 * Возвращает единый формат ошибок для REST API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает ситуацию, когда карта не найдена.
     */
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFound(CardNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Обрабатывает ситуацию, когда клиент не найден.
     */
    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClientNotFound(ClientNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Обрабатывает ситуацию, когда платежная система не найдена в базе.
     */
    @ExceptionHandler(PaymentSystemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentSystemNotFound(PaymentSystemNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Обрабатывает ситуацию, когда для платежной системы
     * не найдена соответствующая процессинговая стратегия.
     */
    @ExceptionHandler(UnsupportedPaymentSystemException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedPaymentSystem(UnsupportedPaymentSystemException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Обрабатывает ошибку недостаточности средств на карте.
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Обрабатывает ошибки некорректных аргументов,
     * например отрицательную или нулевую сумму операции.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Обрабатывает ошибки неверного состояния,
     * например неактивную или просроченную карту.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Обрабатывает ошибки десериализации входного JSON.
     * Например, если передан несуществующий paymentSystemCode.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            Class<?> targetType = invalidFormatException.getTargetType();

            if (targetType != null && targetType.isEnum() && targetType.equals(PaymentSystemCode.class)) {
                String allowedValues = Arrays.stream(PaymentSystemCode.values())
                        .map(Enum::name)
                        .collect(Collectors.joining(", "));

                return buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Invalid paymentSystemCode. Allowed values: " + allowedValues
                );
            }
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed request body");
    }

    /**
     * Обрабатывает все неперехваченные ошибки приложения.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    /**
     * Обрабатывает ситуацию, когда endpoint не найден.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Endpoint not found");
    }

    /**
     * Обрабатывает ситуацию, когда ресурс не найден.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Endpoint not found");
    }


    /**
     * Обрабатывает ошибки недопустимого изменения статуса карты.
     */
    @ExceptionHandler(InvalidCardStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCardStatusTransition(InvalidCardStatusTransitionException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Обрабатывает ошибки валидации входных DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Формирует стандартный DTO ответа об ошибке.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}