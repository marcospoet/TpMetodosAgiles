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
@Transactional
@ActiveProfiles("test")
class LicenciaIntegrationTest {

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
    @DisplayName("Flujo completo: crear titular, emitir licencia y luego buscar por documento")
    void crearTitularEmitirYBuscar() throws Exception {
        // 1. Crear titular
        TitularRecord titular = new TitularRecord(
                "María", "López",
                LocalDate.of(1992, 7, 14),
                TipoDocumento.DNI, "11223344",
                GrupoSanguineo.O, FactorRh.POSITIVO,
                "Calle Falsa 123", false
        );
        String jsonTitular = objectMapper.writeValueAsString(titular);
        var creado = mvc.perform(post("/api/titulares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTitular))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode respTitular = objectMapper.readTree(creado.getResponse().getContentAsString());
        long titularId = respTitular.get("id").asLong();
        String tipoDoc = respTitular.get("tipoDocumento").asText();
        String numDoc  = respTitular.get("numeroDocumento").asText();

        // 2. Emitir licencia clase A
        LicenciaRecord licReq = LicenciaRecord.builder()
                .titularId(titularId)
                .clase(ClaseLicencia.A)
                .emisor("admin@municipio.gob")  // coincide con JWT subject
                .build();
        String jsonLic = objectMapper.writeValueAsString(licReq);
        var emitida = mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLic))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode respLic = objectMapper.readTree(emitida.getResponse().getContentAsString());
        long licenciaId = respLic.get("id").asLong();

        // 3. Buscar por documento en el endpoint /api/licencias/titular
        mvc.perform(get("/api/licencias/titular")
                        .param("tipoDocumento", tipoDoc)
                        .param("numeroDocumento", numDoc))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titular.id").value(titularId))
                .andExpect(jsonPath("$.licencias[0].id").value(licenciaId))
                .andExpect(jsonPath("$.licencias[0].clase").value("A"));
    }

    @Test
    @DisplayName("Debe retornar 404 si no hay licencias para ese documento")
    void buscarPorDocumentoInexistente() throws Exception {
        mvc.perform(get("/api/licencias/titular")
                        .param("tipoDocumento", "DNI")
                        .param("numeroDocumento", "99999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando falta parámetro requerido en /titular")
    void buscarPorDocumentoConParametrosFaltantes() throws Exception {
        mvc.perform(get("/api/licencias/titular")
                        .param("tipoDocumento", "DNI"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Debe listar licencias vencidas correctamente")
    void listarLicenciasVencidas() throws Exception {
        mvc.perform(get("/api/licencias/vencidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Debe contar licencias vencidas correctamente")
    void contarLicenciasVencidas() throws Exception {
        mvc.perform(get("/api/licencias/vencidas/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("Debe contar total de licencias emitidas correctamente")
    void contarTotalLicenciasEmitidas() throws Exception {
        mvc.perform(get("/api/licencias/emitidas/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("Debe retornar 409 al intentar emitir licencia duplicada")
    void emitirLicenciaDuplicada() throws Exception {
        // Crear titular
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

        long titularId = objectMapper
                .readTree(creado.getResponse().getContentAsString())
                .get("id").asLong();

        // Emitir primera licencia clase B
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

        // Intentar emitir duplicada → 409 Conflict
        mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLic))
                .andExpect(status().isConflict());
    }
}
