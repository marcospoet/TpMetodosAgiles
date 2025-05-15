package com.tpagiles.app_licencia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import com.tpagiles.app_licencia.model.Licencia;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "titular",
        "clase",
        "vigenciaAnios",
        "fechaEmision",
        "fechaVencimiento",
        "costo",
        "numeroCopia",
        "motivoCopia",
        "vigente",
        "emisorId"
})
public record LicenciaResponseRecord(

        @Schema(description = "Identificador único de la licencia", example = "123")
        Long id,

        @Schema(description = "Datos del titular asociado")
        TitularResponseRecord titular,

        @Schema(description = "Clase de licencia emitida", example = "A")
        String clase,

        @Schema(description = "Cantidad de años de vigencia", example = "5")
        int vigenciaAnios,

        @Schema(description = "Fecha de emisión de la licencia", example = "2025-05-15")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaEmision,

        @Schema(description = "Fecha de vencimiento de la licencia", example = "2030-05-15")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaVencimiento,

        @Schema(description = "Costo administrativo de la licencia", example = "8.0")
        double costo,

        @Schema(description = "Número de copia si aplica", example = "2")
        Integer numeroCopia,

        @Schema(description = "Motivo por el cual se emite esta copia", example = "Reemplazo por extravío")
        String motivoCopia,

        @Schema(description = "Indicador de si la licencia está vigente", example = "true")
        boolean vigente,

        @Schema(description = "ID del usuario (administrativo) que emitió la licencia", example = "42")
        Long emisorId

) {
    /**
     * Fabrica un LicenciaResponseRecord a partir de la entidad JPA.
     */
    public static LicenciaResponseRecord fromEntity(Licencia lic) {
        return new LicenciaResponseRecord(
                lic.getId(),
                TitularResponseRecord.fromEntity(lic.getTitular()),
                lic.getClase().name(),
                lic.getVigenciaAnios(),
                lic.getFechaEmision(),
                lic.getFechaVencimiento(),
                lic.getCosto(),
                lic.getNumeroCopia(),
                lic.getMotivoCopia(),
                lic.isVigente(),
                lic.getEmisor().getId()
        );
    }
}
