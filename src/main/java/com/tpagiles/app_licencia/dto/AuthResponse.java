package com.tpagiles.app_licencia.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta de autenticación que contiene el JWT.
 */
@Schema(
        name        = "AuthResponse",
        description = "JWT generado tras el login o registro"
)
public record AuthResponse(

        @Schema(
                description = "Token JWT válido para autenticación en endpoints protegidos",
                example     = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String token

) {}
