package com.joka.optima.controller;

import com.joka.optima.dto.request.CardRequestDTO;
import com.joka.optima.dto.response.CardResponseDTO;
import com.joka.optima.enums.CardStatus;
import com.joka.optima.enums.PaymentSystemCode;
import com.joka.optima.exception.CardNotFoundException;
import com.joka.optima.exception.GlobalExceptionHandler;
import com.joka.optima.exception.InsufficientFundsException;
import com.joka.optima.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer тесты для {@link CardController}.
 */
@WebMvcTest(CardController.class)
@Import(GlobalExceptionHandler.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @Test
    void issueCard_shouldReturnCreated() throws Exception {
        CardResponseDTO responseDTO = new CardResponseDTO(
                        1L,
                        "4123456789012345",
                        BigDecimal.ZERO,
                LocalDateTime.now(),
                LocalDate.now().plusYears(4),
                CardStatus.ACTIVE,
                1L,
                PaymentSystemCode.VISA
                );

        when(cardService.issueCard(any(CardRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": 1,
                                  "paymentSystemCode": "VISA"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("4123456789012345"))
                .andExpect(jsonPath("$.paymentSystemCode").value("VISA"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void issueCard_shouldReturnBadRequest_whenPaymentSystemCodeIsInvalid() throws Exception {
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": 1,
                                  "paymentSystemCode": "ELCART1"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void topUp_shouldReturnOk() throws Exception {
        CardResponseDTO responseDTO = new CardResponseDTO(
                        1L,
                        "4123456789012345",
                        BigDecimal.valueOf(1500),
                LocalDateTime.now(),
                LocalDate.now().plusYears(4),
                CardStatus.ACTIVE,
                1L,
                PaymentSystemCode.VISA
                );

        when(cardService.topUp(eq(1L), eq(BigDecimal.valueOf(500)))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/cards/1/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 500
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void debit_shouldReturnBadRequest_whenInsufficientFunds() throws Exception {
        when(cardService.debit(eq(1L), eq(BigDecimal.valueOf(5000))))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(post("/api/cards/1/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 5000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    void changeStatus_shouldReturnOk() throws Exception {
        CardResponseDTO responseDTO = new CardResponseDTO(
                        1L,
                        "4123456789012345",
                        BigDecimal.valueOf(1000),
                LocalDateTime.now(),
                LocalDate.now().plusYears(4),
                CardStatus.BLOCKED,
                1L,
                PaymentSystemCode.VISA
                );

        when(cardService.changeStatus(1L, CardStatus.BLOCKED)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/cards/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "BLOCKED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void getById_shouldReturnNotFound_whenCardDoesNotExist() throws Exception {
        when(cardService.getById(999L))
                .thenThrow(new CardNotFoundException("Card not found with id: 999"));

        mockMvc.perform(get("/api/cards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Card not found with id: 999"));
    }
}