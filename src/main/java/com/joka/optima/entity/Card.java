package com.joka.optima.entity;

import com.joka.optima.enums.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Card extends BaseEntity{

    @Column(name = "card_number")
    private String cardNumber;

    @Column
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    /**
     * Платежная система, определяющая правила обработки платежей по данной карте.
     */
    @ManyToOne
    @JoinColumn(name = "payment_system_id")
    private PaymentSystem paymentSystem;
}
