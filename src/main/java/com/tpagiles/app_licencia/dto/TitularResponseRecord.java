package com.tpagiles.app_licencia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpagiles.app_licencia.model.Titular;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "nombre",
        "apellido",
        "fechaNacimiento",
        "tipoDocumento",
        "numeroDocumento",
        "grupoSanguineo",
        "factorRh",
        "direccion",
        "donanteOrganos"
})
@Schema(description = "Datos de un Titular ya registrado")
public record TitularResponseRecord(

        @Schema(description = "Identificador único del titular", example = "37")
        Long id,

        @Schema(description = "Nombre del titular", example = "Juan")
        String nombre,

        @Schema(description = "Apellido del titular", example = "Pérez")
        String apellido,

        @Schema(description = "Fecha de nacimiento", example = "2000-04-30")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaNacimiento,

        @Schema(description = "Tipo de documento", example = "DNI")
        String tipoDocumento,

        @Schema(description = "Número de documento", example = "12345678")
        String numeroDocumento,

        @Schema(description = "Grupo sanguíneo", example = "O_POSITIVO")
        String grupoSanguineo,

        @Schema(description = "Factor RH", example = "NEGATIVO")
        String factorRh,

        @Schema(description = "Domicilio registrado", example = "San Martín 1234, Ciudad")
        String direccion,

        @Schema(description = "Donante de órganos", example = "true")
        Boolean donanteOrganos

) {
    public static TitularResponseRecord fromEntity(Titular t) {
        return new TitularResponseRecord(
                t.getId(),
                t.getNombre(),
                t.getApellido(),
                t.getFechaNacimiento(),
                t.getTipoDocumento().name(),
                t.getNumeroDocumento(),
                t.getGrupoSanguineo().name(),
                t.getFactorRh().name(),
                t.getDireccion(),
                t.isDonanteOrganos()
        );
    }
}
