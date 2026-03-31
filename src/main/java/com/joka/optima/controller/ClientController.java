package com.joka.optima.controller;

import com.joka.optima.dto.request.ClientRequestDTO;
import com.joka.optima.dto.response.ClientResponseDTO;
import com.joka.optima.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    /**
     * Создает нового клиента.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponseDTO create(@Valid @RequestBody ClientRequestDTO requestDTO) {
        return clientService.create(requestDTO);
    }

    /**
     * Возвращает клиента по идентификатору.
     */
    @GetMapping("/{clientId}")
    public ClientResponseDTO getById(@PathVariable Long clientId) {
        return clientService.getById(clientId);
    }

    /**
     * Возвращает список всех клиентов.
     */
    @GetMapping("/all")
    public List<ClientResponseDTO> getAll() {
        return clientService.getAll();
    }

}
