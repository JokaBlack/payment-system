package com.joka.optima.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientResponseDTO {
    private Long id;
    private String name;
    private String lastName;
}
