package com.joka.optima.service.impl;

import com.joka.optima.dto.request.ClientRequestDTO;
import com.joka.optima.dto.response.ClientResponseDTO;
import com.joka.optima.entity.Client;
import com.joka.optima.exception.ClientNotFoundException;
import com.joka.optima.mapper.ClientMapper;
import com.joka.optima.repository.ClientRepository;
import com.joka.optima.service.ClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDTO create(ClientRequestDTO requestDTO) {
        Client client = clientMapper.toEntity(requestDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponse(savedClient);
    }

    @Override
    public ClientResponseDTO getById(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client not found with id: " + clientId
                ));

        return clientMapper.toResponse(client);
    }

    @Override
    public List<ClientResponseDTO> getAll() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }
}
