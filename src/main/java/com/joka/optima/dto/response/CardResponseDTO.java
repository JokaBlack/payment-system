package com.joka.optima.dto.response;

import com.joka.optima.enums.CardStatus;
import com.joka.optima.enums.PaymentSystemCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CardResponseDTO {
    private Long id;
    private String cardNumber;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDate expirationDate;
    private CardStatus status;
    private Long clientId;
    private PaymentSystemCode paymentSystemCode;
}
