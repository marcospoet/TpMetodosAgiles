package com.tpagiles.app_licencia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "nombre",
        "apellido",
        "tipoDocumento",
        "numeroDocumento",
        "grupoSanguineo",
        "factorRh",
        "donanteOrganos",
        "claseLicencia",
        "fechaVencimiento"
})
@Schema(description = "Titular con al menos una licencia vigente (datos resumidos)")
public record TitularLicenciaVigenteResponseRecord(

        @Schema(description = "Nombre del titular", example = "Juan")
        String nombre,

        @Schema(description = "Apellido del titular", example = "Pérez")
        String apellido,

        @Schema(description = "Tipo de documento", example = "DNI")
        String tipoDocumento,

        @Schema(description = "Número de documento", example = "12345678")
        String numeroDocumento,

        @Schema(description = "Grupo sanguíneo", example = "O")
        String grupoSanguineo,

        @Schema(description = "Factor RH", example = "POSITIVO")
        String factorRh,

        @Schema(description = "Donante de órganos", example = "true")
        Boolean donanteOrganos,

        @Schema(description = "Clase de licencia vigente", example = "B")
        String claseLicencia,

        @Schema(description = "Fecha de vencimiento de la licencia vigente", example = "2028-05-16")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaVencimiento

) {
    // Constructor que acepta ENUMS y hace la conversión a String
    public TitularLicenciaVigenteResponseRecord(String nombre, String apellido,
                                                TipoDocumento tipoDocumento,
                                                String numeroDocumento,
                                                GrupoSanguineo grupoSanguineo,
                                                FactorRh factorRh,
                                                Boolean donanteOrganos,
                                                ClaseLicencia claseLicencia,
                                                LocalDate fechaVencimiento) {
        this(nombre,
                apellido,
                tipoDocumento != null ? tipoDocumento.name() : null,
                numeroDocumento,
                grupoSanguineo != null ? grupoSanguineo.name() : null,
                factorRh != null ? factorRh.name() : null,
                donanteOrganos,
                claseLicencia != null ? claseLicencia.name() : null,
                fechaVencimiento);
    }
}
