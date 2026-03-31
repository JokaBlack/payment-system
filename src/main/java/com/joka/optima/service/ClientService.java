package com.joka.optima.service;

import com.joka.optima.dto.request.ClientRequestDTO;
import com.joka.optima.dto.response.ClientResponseDTO;

import java.util.List;

public interface ClientService {
    /**
     * Создает клиента.
     */
    ClientResponseDTO create(ClientRequestDTO requestDTO);

    /**
     * Возвращает клиента.
     */
    ClientResponseDTO getById(Long clientId);

    /**
     * Возвращает список всех клиентов.
     */
    List<ClientResponseDTO> getAll();
}
