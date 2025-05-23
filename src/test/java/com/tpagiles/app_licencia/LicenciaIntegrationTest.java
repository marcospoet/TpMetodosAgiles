package com.tpagiles.app_licencia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
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
class LicenciaIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Flujo completo: crear titular, emitir licencia y luego buscar por documento")
    void crearTitularEmitirYBuscar() throws Exception {
        // 1. Creamos un Titular
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.numeroDocumento").value("11223344"))
                .andReturn();

        JsonNode respTitular = objectMapper.readTree(creado.getResponse().getContentAsString());
        long titularId = respTitular.get("id").asLong();
        String tipoDoc = respTitular.get("tipoDocumento").asText();
        String numDoc  = respTitular.get("numeroDocumento").asText();

        // 2. Emitimos una licencia clase A
        LicenciaRecord licReq = LicenciaRecord.builder()
                .titularId(titularId)
                .clase(ClaseLicencia.A)
                .emisor("admin")    // debe coincidir con el username cargado en data.sql
                .build();
        String jsonLic = objectMapper.writeValueAsString(licReq);

        var emitida = mvc.perform(post("/api/licencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLic))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titular.id").value(titularId))
                .andExpect(jsonPath("$.clase").value("A"))
                .andReturn();

        JsonNode respLic = objectMapper.readTree(emitida.getResponse().getContentAsString());
        long licenciaId = respLic.get("id").asLong();

        // 3. Buscamos por tipo y número de documento
        mvc.perform(get("/api/licencias/titular")
                        .param("tipoDocumento", tipoDoc)
                        .param("numeroDocumento", numDoc))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.titular.id").value(titularId))
                .andExpect(jsonPath("$.licencias[0].id").value(licenciaId))
                .andExpect(jsonPath("$.licencias[0].clase").value("A"));
    }
}
