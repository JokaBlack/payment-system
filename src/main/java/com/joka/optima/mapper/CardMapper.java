package com.joka.optima.mapper;

import com.joka.optima.dto.response.CardResponseDTO;
import com.joka.optima.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {


    /**
     * Преобразует сущность карты в response DTO.
     */
    public CardResponseDTO toResponse(Card card) {
        return new CardResponseDTO(
                card.getId(),
                card.getCardNumber(),
                card.getBalance(),
                card.getCreatedAt(),
                card.getExpirationDate(),
                card.getStatus(),
                card.getClient().getId(),
                card.getPaymentSystem().getCode()
        );
    }
}
