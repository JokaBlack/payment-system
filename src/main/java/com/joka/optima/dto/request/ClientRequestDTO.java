package com.joka.optima.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientRequestDTO {
    @NotBlank(message = "Client name must not be blank")
    @Size(max = 100, message = "Client name must not exceed 100 characters")
    private String name;
    @NotBlank(message = "Client last name must not be blank")
    @Size(max = 100, message = "Client name must not exceed 100 characters")
    private String lastName;
}
