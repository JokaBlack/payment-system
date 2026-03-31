package com.joka.optima.dto.request;

import com.joka.optima.enums.PaymentSystemCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardRequestDTO {
    private Long clientId;
    private PaymentSystemCode paymentSystemCode;
}
