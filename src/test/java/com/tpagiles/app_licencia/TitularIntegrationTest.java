package com.tpagiles.app_licencia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TitularIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Flujo completo: crear y luego obtener")
    void crearYLuegoObtener() throws Exception {
        // 1. Preparamos el payload
        TitularRecord record = new TitularRecord(
                "Luis", "Fernández",
                LocalDate.of(1975, 12, 1),
                TipoDocumento.PASAPORTE, "X1234567",
                GrupoSanguineo.B, FactorRh.NEGATIVO,
                "Calle Real 456", true
        );
        String json = objectMapper.writeValueAsString(record);

        // 2. Llamada POST → 201 CREATED
        var result = mvc.perform(post("/api/titulares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Luis"))
                .andExpect(jsonPath("$.numeroDocumento").value("X1234567"))
                .andReturn();

        // 3. Extraemos el id del response
        JsonNode resp = objectMapper.readTree(result.getResponse().getContentAsString());
        Long id = resp.get("id").asLong();

        // 4. Llamada GET /api/titulares/{id} → 200 OK y datos coincidentes
        mvc.perform(get("/api/titulares/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Luis"))
                .andExpect(jsonPath("$.numeroDocumento").value("X1234567"));
    }
}
