package com.joka.optima.dto.request;

import com.joka.optima.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CardStatusChangeRequestDTO {
    private CardStatus status;
}
