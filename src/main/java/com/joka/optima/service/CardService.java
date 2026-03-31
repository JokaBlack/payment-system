package com.joka.optima.service;

import com.joka.optima.dto.request.CardRequestDTO;
import com.joka.optima.dto.response.CardResponseDTO;
import com.joka.optima.enums.CardStatus;

import java.math.BigDecimal;

public interface CardService {
    /**
     * Выпускает новую карту для клиента в рамках указанной платежной системы.
     */
    CardResponseDTO issueCard(CardRequestDTO requestDTO);

    /**
     * Пополняет баланс карты.
     */
    CardResponseDTO topUp(Long cardId, BigDecimal amount);

    /**
     * Списывает средства с карты.
     */
    CardResponseDTO debit(Long cardId, BigDecimal amount);

    /**
     * Возвращает данные карты по идентификатору.
     */
    CardResponseDTO getById(Long cardId);

    /**
     * Меняет статус карты.
     */
    CardResponseDTO changeStatus(Long cardId, CardStatus newStatus);
}
