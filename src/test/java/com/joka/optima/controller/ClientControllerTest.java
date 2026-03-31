package com.joka.optima.controller;

import com.joka.optima.dto.response.ClientResponseDTO;
import com.joka.optima.exception.ClientNotFoundException;
import com.joka.optima.exception.GlobalExceptionHandler;
import com.joka.optima.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC-тесты для {@link ClientController}.
 */
@WebMvcTest(ClientController.class)
@Import(GlobalExceptionHandler.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Test
    void create_shouldReturnCreated() throws Exception {
        ClientResponseDTO responseDTO = new ClientResponseDTO(1L, "Aibek", "Asanov");

        when(clientService.create(org.mockito.ArgumentMatchers.any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Aibek",
                                  "lastName": "Asanov"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Aibek"));
    }

    @Test
    void create_shouldReturnBadRequest_whenFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "lastName": ""
                                }
                                """))
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

        mockMvc.perform(get("/api/clients/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aibek"))
                .andExpect(jsonPath("$[1].name").value("Bek"));
    }
}