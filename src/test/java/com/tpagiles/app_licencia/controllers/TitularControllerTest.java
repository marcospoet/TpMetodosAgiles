package com.tpagiles.app_licencia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.dto.TitularResponseRecord;
import com.tpagiles.app_licencia.exception.ResourceAlreadyExistsException;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import com.tpagiles.app_licencia.service.ITitularService;
import com.tpagiles.app_licencia.service.JwtService;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TitularController.class)
@AutoConfigureMockMvc(addFilters = false)  // deshabilito filtros de seguridad para no inyectar JwtAuthenticationFilter
class TitularControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ITitularService titularService;

    @MockitoBean
    private JwtService jwtService;          // para que no falte el bean en el contexto

    @Autowired
    private ObjectMapper objectMapper;

    private TitularRecord validRecord;
    private TitularResponseRecord expectedResponse;

    @BeforeEach
    void setup() {
        // preparo un JWT válido (no se usa en el test porque addFilters=false)
            given(jwtService.validateToken(any())).willReturn(true);
            given(jwtService.getSubject(any())).willReturn("admin@municipio.gob");
            DefaultClaims claims = new DefaultClaims();
            claims.put("roles", List.of("SUPER_USER"));
            given(jwtService.parseClaims(any())).willReturn(claims);
        validRecord = new TitularRecord(
                "Ana", "García",
                LocalDate.of(1985, 5, 20),
                TipoDocumento.DNI,
                "87654321",
                GrupoSanguineo.AB,
                FactorRh.NEGATIVO,
                "Av. Siempre Viva 742",
                false
        );
        expectedResponse = new TitularResponseRecord(
                100L, "Ana", "García",
                LocalDate.of(1985,5,20),
                "DNI", "87654321",
                "AB", "NEGATIVO",
                "Av. Siempre Viva 742", false
        );
    }

    @Test
    @DisplayName("POST /api/titulares → 201 CREATED")
    void crearTitular_exito() throws Exception {
        Titular entidad = Titular.builder()
                .tipoDocumento(validRecord.tipoDocumento())
                .numeroDocumento(validRecord.numeroDocumento())
                .grupoSanguineo(validRecord.grupoSanguineo())
                .factorRh(validRecord.factorRh())
                .direccion(validRecord.direccion())
                .donanteOrganos(validRecord.donanteOrganos())
                .build();
        entidad.setId(expectedResponse.id());
        entidad.setNombre(expectedResponse.nombre());
        entidad.setApellido(expectedResponse.apellido());
        entidad.setFechaNacimiento(expectedResponse.fechaNacimiento());

        given(titularService.createTitular(any())).willReturn(entidad);

        mvc.perform(post("/api/titulares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecord)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.tipoDocumento").value("DNI"));
    }

    @Test
    @DisplayName("POST /api/titulares → 400 BAD REQUEST por validación")
    void crearTitular_validationError() throws Exception {
        var invalid = new TitularRecord(
                "", validRecord.apellido(),
                validRecord.fechaNacimiento(), validRecord.tipoDocumento(),
                validRecord.numeroDocumento(), validRecord.grupoSanguineo(),
                validRecord.factorRh(), validRecord.direccion(),
                validRecord.donanteOrganos()
        );

        mvc.perform(post("/api/titulares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("nombre: El nombre no puede estar vacío"));
    }

    @Test
    @DisplayName("POST /api/titulares → 409 CONFLICT por DNI duplicado")
    void crearTitular_duplicate() throws Exception {
        given(titularService.createTitular(any()))
                .willThrow(new ResourceAlreadyExistsException("Ya existe un Titular con documento: 87654321"));

        mvc.perform(post("/api/titulares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecord)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Ya existe un Titular con documento: 87654321"));
    }

    @Test
    @DisplayName("POST /api/titulares → 500 INTERNAL SERVER ERROR genérico")
    void crearTitular_unexpectedError() throws Exception {
        given(titularService.createTitular(any()))
                .willThrow(new RuntimeException("boom"));

        mvc.perform(post("/api/titulares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecord)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error interno del servidor"));
    }
}
