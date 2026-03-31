package com.joka.optima.controller;

import com.joka.optima.dto.request.CardBalanceOperationRequestDTO;
import com.joka.optima.dto.request.CardRequestDTO;
import com.joka.optima.dto.request.CardStatusChangeRequestDTO;
import com.joka.optima.dto.response.CardResponseDTO;
import com.joka.optima.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    /**
     * Выпускает новую карту для клиента.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponseDTO issueCard(@RequestBody CardRequestDTO requestDTO) {
        return cardService.issueCard(requestDTO);
    }

    /**
     * Пополняет баланс карты.
     */
    @PostMapping("/{cardId}/top-up")
    public CardResponseDTO topUp(@PathVariable Long cardId,
                                 @RequestBody CardBalanceOperationRequestDTO requestDTO) {
        return cardService.topUp(cardId, requestDTO.getAmount());
    }

    /**
     * Выполняет списание средств с карты.
     */
    @PostMapping("/{cardId}/debit")
    public CardResponseDTO debit(@PathVariable Long cardId,
                                 @RequestBody CardBalanceOperationRequestDTO requestDTO) {
        return cardService.debit(cardId, requestDTO.getAmount());
    }

    /**
     * Возвращает данные карты по идентификатору.
     */
    @GetMapping("/{cardId}")
    public CardResponseDTO getById(@PathVariable Long cardId) {
        return cardService.getById(cardId);
    }

    /**
     * Изменяет статус карты.
     */
    @PatchMapping("/{cardId}/status")
    public CardResponseDTO changeStatus(@PathVariable Long cardId,
                                        @RequestBody CardStatusChangeRequestDTO requestDTO) {
        return cardService.changeStatus(cardId, requestDTO.getStatus());
    }
}
