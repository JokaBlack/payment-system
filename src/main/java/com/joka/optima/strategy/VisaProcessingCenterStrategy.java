package com.joka.optima.strategy;

import com.joka.optima.entity.Card;
import com.joka.optima.enums.CardStatus;
import com.joka.optima.enums.PaymentSystemCode;
import com.joka.optima.validation.CardOperationValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class VisaProcessingCenterStrategy implements CardProcessingCenterStrategy {
    private static final String VISA_PREFIX = "4";
    private static final int VALID_YEARS = 4;

    private final int cardNumberLength;
    private final CardOperationValidator validator;

    public VisaProcessingCenterStrategy(
            @Value("${card.number.length}") int cardNumberLength,
            CardOperationValidator validator
    ) {
        this.cardNumberLength = cardNumberLength;
        this.validator = validator;
    }

    @Override
    public PaymentSystemCode supportedSystem() {
        return PaymentSystemCode.VISA;
    }

    @Override
    public void issueCard(Card card) {
        validator.validateCard(card);
        card.setCardNumber(generateCardNumber());
        card.setExpirationDate(LocalDate.now().plusYears(VALID_YEARS));
    }

    @Override
    public void topUp(Card card, BigDecimal amount) {
        validator.validateCard(card);
        validator.validateAmount(amount);
        validator.validateCardAvailability(card);

        card.setBalance(card.getBalance().add(amount));
    }

    @Override
    public void debit(Card card, BigDecimal amount) {
        validator.validateCard(card);
        validator.validateAmount(amount);
        validator.validateCardAvailability(card);
        validator.validateSufficientBalance(card, amount);

        card.setBalance(card.getBalance().subtract(amount));
    }

    @Override
    public void changeStatus(Card card, CardStatus newStatus) {
        validator.validateStatusTransition(card, newStatus);
        card.setStatus(newStatus);
    }

    private String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder(VISA_PREFIX);

        while (cardNumber.length() < cardNumberLength) {
            cardNumber.append(ThreadLocalRandom.current().nextInt(10));
        }

        return cardNumber.toString();
    }
}
