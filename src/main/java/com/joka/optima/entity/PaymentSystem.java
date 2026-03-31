package com.joka.optima.entity;

import com.joka.optima.enums.PaymentSystemCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "payment_systems")
public class PaymentSystem extends BaseEntity{

    @Column(name = "name")
    private String name;

    /**
     * Справочный объект платежной системы, определяющий тип карты и правила выбора способа обработки платежа.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PaymentSystemCode code;
}
