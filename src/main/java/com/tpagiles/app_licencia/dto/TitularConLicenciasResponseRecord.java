package com.tpagiles.app_licencia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "titular", "licencias" })
@Schema(description = "Datos de un titular junto con sus licencias")
public record TitularConLicenciasResponseRecord(

        @Schema(description = "Datos del titular")
        TitularResponseRecord titular,

        @Schema(description = "Listado de licencias asociadas al titular (cada una incluye sólo el titularId como referencia)")
        List<LicenciaResponseRecord> licencias

) {
    /**
     * Crea un DTO que agrupa los datos del titular y su lista de licencias.
     *
     * @param titularDto   DTO con los datos completos del titular
     * @param licenciasDto Lista de licencias (cada una con titularId, clase, fechas, costo, emisorId, vigente)
     */
    public static TitularConLicenciasResponseRecord from(
            TitularResponseRecord titularDto,
            List<LicenciaResponseRecord> licenciasDto
    ) {
        return new TitularConLicenciasResponseRecord(titularDto, licenciasDto);
    }
}
