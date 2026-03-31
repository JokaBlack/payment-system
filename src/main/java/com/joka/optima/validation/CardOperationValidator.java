package com.joka.optima.validation;
import com.joka.optima.entity.Card;
import com.joka.optima.enums.CardStatus;
import com.joka.optima.exception.InsufficientFundsException;
import com.joka.optima.exception.InvalidCardStatusTransitionException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * Компонент для валидации операций по банковской карте.
 */
@Component
public class CardOperationValidator {
    /**
     * Проверяет корректность карты.
     */
    public void validateCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card must not be null");
        }

        if (card.getBalance() == null) {
            throw new IllegalStateException("Card balance is not initialized");
        }
    }

    /**
     * Проверяет корректность суммы операции.
     */
    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    /**
     * Проверяет доступность карты для операций.
     */
    public void validateCardAvailability(Card card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is not active");
        }

        if (card.getExpirationDate() != null && card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Card has expired");
        }
    }

    /**
     * Проверяет достаточность средств на карте.
     */
    public void validateSufficientBalance(Card card, BigDecimal amount) {
        if (card.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
    }

    /**
     * Проверяет корректность перехода статуса карты.
     */
    public void validateStatusTransition(Card card, CardStatus newStatus) {
        if (card == null) {
            throw new IllegalArgumentException("Card must not be null");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("Card status must not be null");
        }

        CardStatus currentStatus = card.getStatus();

        if (currentStatus == newStatus) {
            throw new InvalidCardStatusTransitionException(
                    "Card already has status: " + currentStatus
            );
        }

        if (currentStatus == CardStatus.CLOSED) {
            throw new InvalidCardStatusTransitionException(
                    "Closed card status cannot be changed"
            );
        }

        if (card.getExpirationDate() != null
                && card.getExpirationDate().isBefore(LocalDate.now())
                && newStatus != CardStatus.CLOSED) {
            throw new InvalidCardStatusTransitionException(
                    "Expired card can only be changed to CLOSED status"
            );
        }
    }
}
