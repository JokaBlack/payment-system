package com.joka.optima.dto.request;

import com.joka.optima.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardStatusChangeRequestDTO {
    private CardStatus status;
}
