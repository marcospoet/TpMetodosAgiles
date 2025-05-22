package com.tpagiles.app_licencia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "titularId",
        "clase",
        "numeroCopia",
        "motivoCopia",
        "emisor"
})
@Schema(description = "Datos necesarios para emitir una licencia (clases A o B)")
@Builder
public record LicenciaRecord(

        @Schema(
                description = "ID del titular que solicita la licencia",
                example = "42",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Debe indicar el ID del titular")
        @Positive(message = "El ID del titular debe ser un número positivo")
        Long titularId,

        @Schema(
                description = "Clase de licencia a emitir (A o B)",
                example = "A",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Debe elegir la clase de licencia (A o B)")
        ClaseLicencia clase,

        @Schema(
                description = "Número de copia si se trata de una copia de la licencia",
                example = "2",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Positive(message = "El número de copia debe ser mayor que cero")
        Integer numeroCopia,

        @Schema(
                description = "Motivo por el cual se emite esta copia",
                example = "Extravío de la licencia",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 200, message = "El motivo de copia no puede superar 200 caracteres")
        String motivoCopia,

        @Schema(
                description = "Username del usuario que emite la licencia",
                example = "admin",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Debe indicar el username del emisor")
        String emisor
) {
}