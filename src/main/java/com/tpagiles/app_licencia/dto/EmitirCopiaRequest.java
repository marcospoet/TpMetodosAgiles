package com.tpagiles.app_licencia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para emitir una copia de una licencia existente")
public record EmitirCopiaRequest(

        @Schema(description = "ID de la licencia original", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Debe indicar la licencia original")
        @Positive
        Long licenciaOriginalId,

        @Schema(description = "Motivo de la copia", example = "Robo del carnet", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 200)
        String motivo,

        @Schema(description = "Username del emisor", example = "admin@municipio.gob", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String emisor
) {}
