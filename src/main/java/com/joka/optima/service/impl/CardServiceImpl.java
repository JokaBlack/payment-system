package com.joka.optima.service.impl;

import com.joka.optima.dto.request.CardRequestDTO;
import com.joka.optima.dto.response.CardResponseDTO;
import com.joka.optima.entity.Card;
import com.joka.optima.entity.Client;
import com.joka.optima.entity.PaymentSystem;
import com.joka.optima.enums.CardStatus;
import com.joka.optima.exception.CardNotFoundException;
import com.joka.optima.exception.ClientNotFoundException;
import com.joka.optima.exception.PaymentSystemNotFoundException;
import com.joka.optima.mapper.CardMapper;
import com.joka.optima.repository.CardRepository;
import com.joka.optima.repository.ClientRepository;
import com.joka.optima.repository.PaymentSystemRepository;
import com.joka.optima.service.CardService;
import com.joka.optima.strategy.CardProcessingCenterStrategy;
import com.joka.optima.strategy.CardProcessingCenterStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ClientRepository clientRepository;
    private final PaymentSystemRepository paymentSystemRepository;
    private final CardProcessingCenterStrategyResolver strategyResolver;
    private final CardMapper cardMapper;

    /**
     * Выпускает новую карту для клиента в рамках выбранной платежной системы.
     */
    @Override
    public CardResponseDTO issueCard(CardRequestDTO requestDTO) {
        Client client = clientRepository.findById(requestDTO.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client not found with id: " + requestDTO.getClientId()
                ));

        PaymentSystem paymentSystem = paymentSystemRepository.findByCode(requestDTO.getPaymentSystemCode())
                .orElseThrow(() -> new PaymentSystemNotFoundException(
                        "Payment system not found with code: " + requestDTO.getPaymentSystemCode()
                ));

        Card card = new Card();
        card.setClient(client);
        card.setPaymentSystem(paymentSystem);

        CardProcessingCenterStrategy strategy = strategyResolver.resolve(paymentSystem.getCode());
        strategy.issueCard(card);

        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponse(savedCard);
    }

    /**
     * Пополняет баланс карты.
     */
    @Override
    public CardResponseDTO topUp(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + cardId
                ));

        CardProcessingCenterStrategy strategy = strategyResolver.resolve(card.getPaymentSystem().getCode());
        strategy.topUp(card, amount);

        Card updatedCard = cardRepository.save(card);
        return cardMapper.toResponse(updatedCard);
    }

    /**
     * Выполняет списание средств с карты.
     */
    @Override
    public CardResponseDTO debit(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + cardId
                ));

        CardProcessingCenterStrategy strategy = strategyResolver.resolve(card.getPaymentSystem().getCode());
        strategy.debit(card, amount);

        Card updatedCard = cardRepository.save(card);
        return cardMapper.toResponse(updatedCard);
    }

    /**
     * Возвращает данные карты по идентификатору.
     */
    @Override
    public CardResponseDTO getById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + cardId
                ));

        return cardMapper.toResponse(card);
    }

    @Override
    public CardResponseDTO changeStatus(Long cardId, CardStatus newStatus) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + cardId
                ));

        CardProcessingCenterStrategy strategy =
                strategyResolver.resolve(card.getPaymentSystem().getCode());

        strategy.changeStatus(card, newStatus);

        Card updatedCard = cardRepository.save(card);
        return cardMapper.toResponse(updatedCard);
    }
}
