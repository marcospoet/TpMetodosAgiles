package com.tpagiles.app_licencia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "Respuesta de error")
public record ErrorResponse(
        @Schema(description = "Timestamp del error", example = "2025-05-15T10:00:00Z")
        OffsetDateTime timestamp,

        @Schema(description = "Código HTTP", example = "400")
        int status,

        @Schema(description = "Mensaje descriptivo", example = "Validación de campo nombre fallida")
        String message
) {}
