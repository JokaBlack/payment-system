package com.joka.optima.validation;

import com.joka.optima.entity.Card;
import com.joka.optima.entity.Client;
import com.joka.optima.entity.PaymentSystem;
import com.joka.optima.enums.CardStatus;
import com.joka.optima.enums.PaymentSystemCode;
import com.joka.optima.exception.InsufficientFundsException;
import com.joka.optima.exception.InvalidCardStatusTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для {@link CardOperationValidator}.
 */
class CardOperationValidatorTest {

    private CardOperationValidator validator;
    private Card card;

    @BeforeEach
    void setUp() {
        validator = new CardOperationValidator();

        Client client = new Client();
        client.setId(1L);
        client.setName("Aibek");
        client.setLastName("Asanov");

        PaymentSystem paymentSystem = new PaymentSystem();
        paymentSystem.setId(1L);
        paymentSystem.setCode(PaymentSystemCode.VISA);
        paymentSystem.setName("Visa");

        card = new Card();
        card.setId(1L);
        card.setCardNumber("4123456789012345");
        card.setBalance(BigDecimal.valueOf(1000));
        card.setStatus(CardStatus.ACTIVE);
        card.setExpirationDate(LocalDate.now().plusYears(4));
        card.setClient(client);
        card.setPaymentSystem(paymentSystem);
    }

    @Test
    void validateCard_shouldPass_whenCardIsValid() {
        assertDoesNotThrow(() -> validator.validateCard(card));
    }

    @Test
    void validateCard_shouldThrow_whenCardIsNull() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateCard(null));
    }

    @Test
    void validateCard_shouldThrow_whenBalanceIsNull() {
        card.setBalance(null);

        assertThrows(IllegalStateException.class, () -> validator.validateCard(card));
    }

    @Test
    void validateAmount_shouldPass_whenAmountIsPositive() {
        assertDoesNotThrow(() -> validator.validateAmount(BigDecimal.valueOf(100)));
    }

    @Test
    void validateAmount_shouldThrow_whenAmountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateAmount(null));
    }

    @Test
    void validateAmount_shouldThrow_whenAmountIsZero() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateAmount(BigDecimal.ZERO));
    }

    @Test
    void validateAmount_shouldThrow_whenAmountIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validateAmount(BigDecimal.valueOf(-1)));
    }

    @Test
    void validateCardAvailability_shouldPass_whenCardIsActiveAndNotExpired() {
        assertDoesNotThrow(() -> validator.validateCardAvailability(card));
    }

    @Test
    void validateCardAvailability_shouldThrow_whenCardIsBlocked() {
        card.setStatus(CardStatus.BLOCKED);

        assertThrows(IllegalStateException.class, () -> validator.validateCardAvailability(card));
    }

    @Test
    void validateCardAvailability_shouldThrow_whenCardIsExpired() {
        card.setExpirationDate(LocalDate.now().minusDays(1));

        assertThrows(IllegalStateException.class, () -> validator.validateCardAvailability(card));
    }

    @Test
    void validateSufficientBalance_shouldPass_whenEnoughFunds() {
        assertDoesNotThrow(() ->
                validator.validateSufficientBalance(card, BigDecimal.valueOf(500)));
    }

    @Test
    void validateSufficientBalance_shouldThrow_whenInsufficientFunds() {
        assertThrows(InsufficientFundsException.class,
                () -> validator.validateSufficientBalance(card, BigDecimal.valueOf(5000)));
    }

    @Test
    void validateStatusTransition_shouldPass_whenActiveToBlocked() {
        assertDoesNotThrow(() ->
                validator.validateStatusTransition(card, CardStatus.BLOCKED));
    }

    @Test
    void validateStatusTransition_shouldThrow_whenNewStatusIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validateStatusTransition(card, null));
    }

    @Test
    void validateStatusTransition_shouldThrow_whenSameStatus() {
        assertThrows(InvalidCardStatusTransitionException.class,
                () -> validator.validateStatusTransition(card, CardStatus.ACTIVE));
    }

    @Test
    void validateStatusTransition_shouldThrow_whenClosedCardChangesStatus() {
        card.setStatus(CardStatus.CLOSED);

        assertThrows(InvalidCardStatusTransitionException.class,
                () -> validator.validateStatusTransition(card, CardStatus.ACTIVE));
    }

    @Test
    void validateStatusTransition_shouldThrow_whenExpiredCardChangesToNonClosed() {
        card.setExpirationDate(LocalDate.now().minusDays(1));

        assertThrows(InvalidCardStatusTransitionException.class,
                () -> validator.validateStatusTransition(card, CardStatus.BLOCKED));
    }

    @Test
    void validateStatusTransition_shouldPass_whenExpiredCardChangesToClosed() {
        card.setExpirationDate(LocalDate.now().minusDays(1));

        assertDoesNotThrow(() ->
                validator.validateStatusTransition(card, CardStatus.CLOSED));
    }
}