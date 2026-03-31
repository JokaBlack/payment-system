package com.joka.optima.strategy;

import com.joka.optima.entity.Card;
import com.joka.optima.enums.CardStatus;
import com.joka.optima.enums.PaymentSystemCode;

import java.math.BigDecimal;

/**
 * Договор о стратегии обработки платежей для конкретной платежной системы.
 *Определяет правила выпуска, пополнения и зачисления средств на карты.
 */
public interface CardProcessingCenterStrategy {
    /**
     * Возвращает код платежной системы, поддерживаемой данной стратегией.
     */
    PaymentSystemCode supportedSystem();

    /**
     * Применяет правила выпуска карты для конкретной платежной системы.
     */
    void issueCard(Card card);

    /**
     * Выполняет пополнение карты.
     */
    void topUp(Card card, BigDecimal amount);

    /**
     * Выполняет списание средств с карты.
     */
    void debit(Card card, BigDecimal amount);

    /**
     * Изменяет статус карты по правилам конкретной платежной системы.
     */
    void changeStatus(Card card, CardStatus newStatus);
}
