package com.tpagiles.app_licencia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.exception.ResourceAlreadyExistsException;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.repository.TarifarioLicenciaRepository;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import com.tpagiles.app_licencia.service.ILicenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LicenciaController.class)
@AutoConfigureMockMvc
class LicenciaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockeamos el slice de servicio
    @MockitoBean
    private ILicenciaService licenciaService;

    // Y también los repositorios que usa tu método cargaUsuarios()
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private TarifarioLicenciaRepository tarifarioLicenciaRepository;

    private LicenciaRecord validRecord;
    private LicenciaResponseRecord expectedResponse;

    @BeforeEach
    void setup() {
        validRecord = LicenciaRecord.builder()
                .titularId(42L)
                .clase(ClaseLicencia.A)
                .numeroCopia(2)
                .motivoCopia("Extravío de la licencia")
                .emisor("admin")
                .build();

        expectedResponse = new LicenciaResponseRecord(
                10L,
                null,
                "A",
                5,
                LocalDate.of(2025,5,15),
                LocalDate.of(2030,5,15),
                20.0,
                2,
                "Extravío de la licencia",
                true,
                1L
        );
    }

    @Test
    @DisplayName("POST /api/licencias → 201 CREATED")
    void emitirLicencia_exito() throws Exception {
        when(licenciaService.emitirLicencia(any(LicenciaRecord.class)))
                .thenReturn(expectedResponse);

        mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecord)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.clase").value("A"))
                .andExpect(jsonPath("$.costo").value(20.0))
                .andExpect(jsonPath("$.numeroCopia").value(2))
                .andExpect(jsonPath("$.motivoCopia").value("Extravío de la licencia"));
    }

    @Test
    @DisplayName("POST /api/licencias → 400 BAD REQUEST por validación")
    void emitirLicencia_validationError() throws Exception {
        var invalid = LicenciaRecord.builder()
                .titularId(null)
                .clase(null)
                .numeroCopia(-1)
                .motivoCopia("X".repeat(201))
                .emisor("")
                .build();

        mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/licencias → 409 CONFLICT por licencia vigente duplicada")
    void emitirLicencia_conflict() throws Exception {
        when(licenciaService.emitirLicencia(any()))
                .thenThrow(new ResourceAlreadyExistsException("Ya hay una licencia vigente para este titular"));

        mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecord)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Ya hay una licencia vigente para este titular"));
    }

    @Test
    @DisplayName("POST /api/licencias → 500 INTERNAL SERVER ERROR genérico")
    void emitirLicencia_unexpectedError() throws Exception {
        when(licenciaService.emitirLicencia(any()))
                .thenThrow(new RuntimeException("boom"));

        mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecord)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Error interno del servidor"));
    }
}

