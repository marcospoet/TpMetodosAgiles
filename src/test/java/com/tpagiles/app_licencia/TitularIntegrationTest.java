package com.tpagiles.app_licencia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpagiles.app_licencia.config.SecurityConfig;
import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import com.tpagiles.app_licencia.security.JwtAuthenticationFilter;
import com.tpagiles.app_licencia.service.JwtService;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
@ActiveProfiles("test")
@Transactional
class TitularIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String VALID_TOKEN = "miTokenValido";

    @BeforeEach
    void setup() {
        // Simular JWT válido y roles
        given(jwtService.validateToken(VALID_TOKEN)).willReturn(true);
        given(jwtService.getSubject(VALID_TOKEN)).willReturn("admin@municipio.gob");
        DefaultClaims claims = new DefaultClaims();
        claims.put("roles", List.of("SUPER_USER"));
        given(jwtService.parseClaims(VALID_TOKEN)).willReturn(claims);
    }

    @Test
    @DisplayName("Flujo completo: crear y luego obtener titular")
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

    @Test
    @DisplayName("Debe contar licencias vencidas correctamente")
    void contarLicenciasVencidas() throws Exception {
        mvc.perform(get("/api/licencias/vencidas/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("Debe retornar error al intentar emitir licencia duplicada")
    void emitirLicenciaDuplicada() throws Exception {
        // 1. Crear titular
        TitularRecord titular = new TitularRecord(
                "Juan", "Pérez",
                LocalDate.of(1990, 5, 20),
                TipoDocumento.DNI, "12345678",
                GrupoSanguineo.A, FactorRh.POSITIVO,
                "Calle Real 456", false
        );
        String jsonTitular = objectMapper.writeValueAsString(titular);

        var creado = mvc.perform(post("/api/titulares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTitular))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode respTitular = objectMapper.readTree(creado.getResponse().getContentAsString());
        long titularId = respTitular.get("id").asLong();

        // 2. Emitir primera licencia
        LicenciaRecord licReq = LicenciaRecord.builder()
                .titularId(titularId)
                .clase(ClaseLicencia.B)
                .emisor("admin@municipio.gob")
                .build();
        String jsonLic = objectMapper.writeValueAsString(licReq);

        mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLic))
                .andExpect(status().isCreated());

        // 3. Intentar emitir licencia duplicada (misma clase)
        mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLic))
                .andExpect(status().isConflict()); // Esperamos un 409 Conflict
    }
}