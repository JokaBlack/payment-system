package com.joka.optima.service.impl;

import com.joka.optima.dto.request.ClientRequestDTO;
import com.joka.optima.dto.response.ClientResponseDTO;
import com.joka.optima.entity.Client;
import com.joka.optima.exception.ClientNotFoundException;
import com.joka.optima.mapper.ClientMapper;
import com.joka.optima.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для {@link ClientServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void create_shouldCreateClientSuccessfully() {
        ClientRequestDTO requestDTO = new ClientRequestDTO("Aibek", "Asanov");

        Client client = new Client();
        client.setName("Aibek");
        client.setLastName("Asanov");

        Client savedClient = new Client();
        savedClient.setId(1L);
        savedClient.setName("Aibek");
        savedClient.setLastName("Asanov");

        ClientResponseDTO responseDTO = new ClientResponseDTO(1L, "Aibek", "Asanov");

        when(clientMapper.toEntity(requestDTO)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(savedClient);
        when(clientMapper.toResponse(savedClient)).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Aibek", result.getName());
        assertEquals("Asanov", result.getLastName());

        verify(clientMapper).toEntity(requestDTO);
        verify(clientRepository).save(client);
        verify(clientMapper).toResponse(savedClient);
    }

    @Test
    void getById_shouldReturnClientSuccessfully() {
        Client client = new Client();
        client.setId(1L);
        client.setName("Aibek");
        client.setLastName("Asanov");

        ClientResponseDTO responseDTO = new ClientResponseDTO(1L, "Aibek", "Asanov");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Aibek", result.getName());

        verify(clientRepository).findById(1L);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void getById_shouldThrowWhenClientNotFound() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.getById(999L));

        verify(clientRepository).findById(999L);
        verifyNoMoreInteractions(clientMapper);
    }

    @Test
    void getAll_shouldReturnAllClients() {
        Client client1 = new Client();
        client1.setId(1L);
        client1.setName("Aibek");
        client1.setLastName("Asanov");

        Client client2 = new Client();
        client2.setId(2L);
        client2.setName("Bek");
        client2.setLastName("Nazarov");

        ClientResponseDTO responseDTO1 = new ClientResponseDTO(1L, "Aibek", "Asanov");
        ClientResponseDTO responseDTO2 = new ClientResponseDTO(2L, "Bek", "Nazarov");

        when(clientRepository.findAll()).thenReturn(List.of(client1, client2));
        when(clientMapper.toResponse(client1)).thenReturn(responseDTO1);
        when(clientMapper.toResponse(client2)).thenReturn(responseDTO2);

        List<ClientResponseDTO> result = clientService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Aibek", result.get(0).getName());
        assertEquals("Bek", result.get(1).getName());

        verify(clientRepository).findAll();
        verify(clientMapper).toResponse(client1);
        verify(clientMapper).toResponse(client2);
    }
}