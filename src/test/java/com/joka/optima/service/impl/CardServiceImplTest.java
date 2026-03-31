package com.joka.optima.service.impl;

import com.joka.optima.dto.request.CardRequestDTO;
import com.joka.optima.dto.response.CardResponseDTO;
import com.joka.optima.entity.Card;
import com.joka.optima.entity.Client;
import com.joka.optima.entity.PaymentSystem;
import com.joka.optima.enums.CardStatus;
import com.joka.optima.enums.PaymentSystemCode;
import com.joka.optima.exception.CardNotFoundException;
import com.joka.optima.exception.ClientNotFoundException;
import com.joka.optima.exception.InsufficientFundsException;
import com.joka.optima.exception.PaymentSystemNotFoundException;
import com.joka.optima.mapper.CardMapper;
import com.joka.optima.repository.CardRepository;
import com.joka.optima.repository.ClientRepository;
import com.joka.optima.repository.PaymentSystemRepository;
import com.joka.optima.strategy.CardProcessingCenterStrategy;
import com.joka.optima.strategy.CardProcessingCenterStrategyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для {@link CardServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PaymentSystemRepository paymentSystemRepository;

    @Mock
    private CardProcessingCenterStrategyResolver strategyResolver;

    @Mock
    private CardProcessingCenterStrategy strategy;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private Client client;
    private PaymentSystem paymentSystem;
    private Card card;
    private CardResponseDTO cardResponseDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("Aibek");
        client.setLastName("Asanov");

        paymentSystem = new PaymentSystem();
        paymentSystem.setId(1L);
        paymentSystem.setCode(PaymentSystemCode.VISA);
        paymentSystem.setName("Visa");

        card = new Card();
        card.setId(1L);
        card.setCardNumber("4123456789012345");
        card.setBalance(BigDecimal.valueOf(1000));
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());
        card.setExpirationDate(LocalDate.now().plusYears(4));
        card.setClient(client);
        card.setPaymentSystem(paymentSystem);

        cardResponseDTO = new CardResponseDTO(
                        1L,
                        "4123456789012345",
                        BigDecimal.valueOf(1000),
                card.getCreatedAt(),
                card.getExpirationDate(),
                CardStatus.ACTIVE,
                1L,
                PaymentSystemCode.VISA
                );
    }

    @Test
    void issueCard_shouldCreateCardSuccessfully() {
        CardRequestDTO requestDTO = new CardRequestDTO(1L, PaymentSystemCode.VISA);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(paymentSystemRepository.findByCode(PaymentSystemCode.VISA)).thenReturn(Optional.of(paymentSystem));
        when(strategyResolver.resolve(PaymentSystemCode.VISA)).thenReturn(strategy);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card savedCard = invocation.getArgument(0);
            savedCard.setId(1L);
            return savedCard;
        });
        when(cardMapper.toResponse(any(Card.class))).thenReturn(cardResponseDTO);

        CardResponseDTO result = cardService.issueCard(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(PaymentSystemCode.VISA, result.getPaymentSystemCode());

        verify(clientRepository).findById(1L);
        verify(paymentSystemRepository).findByCode(PaymentSystemCode.VISA);
        verify(strategyResolver).resolve(PaymentSystemCode.VISA);
        verify(strategy).issueCard(any(Card.class));
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toResponse(any(Card.class));
    }

    @Test
    void issueCard_shouldThrowWhenClientNotFound() {
        CardRequestDTO requestDTO = new CardRequestDTO(99L, PaymentSystemCode.VISA);

        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> cardService.issueCard(requestDTO));

        verify(clientRepository).findById(99L);
        verifyNoMoreInteractions(paymentSystemRepository, strategyResolver, cardRepository, cardMapper);
    }

    @Test
    void issueCard_shouldThrowWhenPaymentSystemNotFound() {
        CardRequestDTO requestDTO = new CardRequestDTO(1L, PaymentSystemCode.VISA);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(paymentSystemRepository.findByCode(PaymentSystemCode.VISA)).thenReturn(Optional.empty());

        assertThrows(PaymentSystemNotFoundException.class, () -> cardService.issueCard(requestDTO));

        verify(clientRepository).findById(1L);
        verify(paymentSystemRepository).findByCode(PaymentSystemCode.VISA);
        verifyNoMoreInteractions(strategyResolver, cardRepository, cardMapper);
    }

    @Test
    void topUp_shouldIncreaseBalanceSuccessfully() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(strategyResolver.resolve(PaymentSystemCode.VISA)).thenReturn(strategy);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toResponse(card)).thenReturn(cardResponseDTO);

        CardResponseDTO result = cardService.topUp(1L, BigDecimal.valueOf(500));

        assertNotNull(result);

        verify(cardRepository).findById(1L);
        verify(strategyResolver).resolve(PaymentSystemCode.VISA);
        verify(strategy).topUp(card, BigDecimal.valueOf(500));
        verify(cardRepository).save(card);
        verify(cardMapper).toResponse(card);
    }

    @Test
    void debit_shouldDecreaseBalanceSuccessfully() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(strategyResolver.resolve(PaymentSystemCode.VISA)).thenReturn(strategy);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toResponse(card)).thenReturn(cardResponseDTO);

        CardResponseDTO result = cardService.debit(1L, BigDecimal.valueOf(300));

        assertNotNull(result);

        verify(cardRepository).findById(1L);
        verify(strategyResolver).resolve(PaymentSystemCode.VISA);
        verify(strategy).debit(card, BigDecimal.valueOf(300));
        verify(cardRepository).save(card);
        verify(cardMapper).toResponse(card);
    }

    @Test
    void debit_shouldThrowWhenInsufficientFunds() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(strategyResolver.resolve(PaymentSystemCode.VISA)).thenReturn(strategy);

        doThrow(new InsufficientFundsException("Insufficient funds"))
                .when(strategy).debit(card, BigDecimal.valueOf(5000));

        assertThrows(InsufficientFundsException.class,
                () -> cardService.debit(1L, BigDecimal.valueOf(5000)));

        verify(cardRepository).findById(1L);
        verify(strategyResolver).resolve(PaymentSystemCode.VISA);
        verify(strategy).debit(card, BigDecimal.valueOf(5000));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void changeStatus_shouldChangeStatusSuccessfully() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(strategyResolver.resolve(PaymentSystemCode.VISA)).thenReturn(strategy);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toResponse(card)).thenReturn(cardResponseDTO);

        CardResponseDTO result = cardService.changeStatus(1L, CardStatus.BLOCKED);

        assertNotNull(result);

        verify(cardRepository).findById(1L);
        verify(strategyResolver).resolve(PaymentSystemCode.VISA);
        verify(strategy).changeStatus(card, CardStatus.BLOCKED);
        verify(cardRepository).save(card);
        verify(cardMapper).toResponse(card);
    }

    @Test
    void getById_shouldReturnCardSuccessfully() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toResponse(card)).thenReturn(cardResponseDTO);

        CardResponseDTO result = cardService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(cardRepository).findById(1L);
        verify(cardMapper).toResponse(card);
    }

    @Test
    void getById_shouldThrowWhenCardNotFound() {
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getById(999L));

        verify(cardRepository).findById(999L);
        verifyNoMoreInteractions(cardMapper);
    }
}