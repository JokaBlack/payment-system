package com.joka.optima.controller;

import com.joka.optima.dto.request.ClientRequestDTO;
import com.joka.optima.dto.response.ClientResponseDTO;
import com.joka.optima.exception.ClientNotFoundException;
import com.joka.optima.exception.GlobalExceptionHandler;
import com.joka.optima.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-тесты для {@link ClientController}.
 */
@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        ClientController clientController = new ClientController(clientService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        ClientRequestDTO requestDTO = new ClientRequestDTO("Aibek", "Asanov");
        ClientResponseDTO responseDTO = new ClientResponseDTO(1L, "Aibek", "Asanov");

        when(clientService.create(any(ClientRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Aibek"));
    }

    @Test
    void create_shouldReturnBadRequest_whenFieldsAreBlank() throws Exception {
        String invalidRequest = """
                {
                  "name": "",
                  "lastName": ""
                }
                """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getById_shouldReturnClient() throws Exception {
        ClientResponseDTO responseDTO = new ClientResponseDTO(1L, "Aibek", "Asanov");

        when(clientService.getById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Asanov"));
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        when(clientService.getById(999L))
                .thenThrow(new ClientNotFoundException("Client not found with id: 999"));

        mockMvc.perform(get("/api/clients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client not found with id: 999"));
    }

    @Test
    void getAll_shouldReturnClients() throws Exception {
        List<ClientResponseDTO> clients = List.of(
                new ClientResponseDTO(1L, "Aibek", "Asanov"),
                new ClientResponseDTO(2L, "Bek", "Nazarov")
        );

        when(clientService.getAll()).thenReturn(clients);

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}