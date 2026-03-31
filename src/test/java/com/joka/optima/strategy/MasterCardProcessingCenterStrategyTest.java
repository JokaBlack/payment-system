package com.joka.optima.strategy;

import com.joka.optima.entity.Card;
import com.joka.optima.entity.Client;
import com.joka.optima.entity.PaymentSystem;
import com.joka.optima.enums.CardStatus;
import com.joka.optima.enums.PaymentSystemCode;
import com.joka.optima.exception.InsufficientFundsException;
import com.joka.optima.validation.CardOperationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для {@link MasterCardProcessingCenterStrategy}.
 */
class MasterCardProcessingCenterStrategyTest {

    private MasterCardProcessingCenterStrategy strategy;
    private Card card;

    @BeforeEach
    void setUp() {
        strategy = new MasterCardProcessingCenterStrategy(16, new CardOperationValidator());

        Client client = new Client();
        client.setId(1L);
        client.setName("Aibek");
        client.setLastName("Asanov");

        PaymentSystem paymentSystem = new PaymentSystem();
        paymentSystem.setId(1L);
        paymentSystem.setCode(PaymentSystemCode.MASTERCARD);
        paymentSystem.setName("MasterCard");

        card = new Card();
        card.setId(1L);
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpirationDate(LocalDate.now().plusYears(5));
        card.setClient(client);
        card.setPaymentSystem(paymentSystem);
    }

    @Test
    void supportedSystem_shouldReturnMasterCard() {
        assertEquals(PaymentSystemCode.MASTERCARD, strategy.supportedSystem());
    }

    @Test
    void issueCard_shouldGenerateMasterCardNumberAndExpirationDate() {
        strategy.issueCard(card);

        assertNotNull(card.getCardNumber());
        assertTrue(card.getCardNumber().startsWith("5"));
        assertEquals(16, card.getCardNumber().length());
        assertEquals(LocalDate.now().plusYears(5), card.getExpirationDate());
    }

    @Test
    void topUp_shouldIncreaseBalance() {
        strategy.topUp(card, BigDecimal.valueOf(400));

        assertEquals(BigDecimal.valueOf(400), card.getBalance());
    }

    @Test
    void debit_shouldDecreaseBalance() {
        card.setBalance(BigDecimal.valueOf(1000));

        strategy.debit(card, BigDecimal.valueOf(250));

        assertEquals(BigDecimal.valueOf(750), card.getBalance());
    }

    @Test
    void debit_shouldThrowWhenInsufficientFunds() {
        card.setBalance(BigDecimal.valueOf(50));

        assertThrows(InsufficientFundsException.class,
                () -> strategy.debit(card, BigDecimal.valueOf(200)));
    }

    @Test
    void changeStatus_shouldChangeStatus() {
        strategy.changeStatus(card, CardStatus.BLOCKED);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }
}