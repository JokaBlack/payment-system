package com.joka.optima.mapper;

import com.joka.optima.dto.request.ClientRequestDTO;
import com.joka.optima.dto.response.ClientResponseDTO;
import com.joka.optima.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    /**
     * Преобразует DTO запроса в сущность клиента.
     */
    public Client toEntity(ClientRequestDTO requestDTO) {
        Client client = new Client();
        client.setName(requestDTO.getName());
        client.setLastName(requestDTO.getLastName());
        return client;
    }

    /**
     * Преобразует сущность клиента в DTO ответа.
     */
    public ClientResponseDTO toResponse(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getLastName()
        );
    }
}
