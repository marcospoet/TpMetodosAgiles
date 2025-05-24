package com.tpagiles.app_licencia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Credenciales enviadas al endpoint de autenticación.
 */
@Schema(
        name = "AuthRequest",
        description = "Usuario y contraseña para solicitar un token JWT"
)
public record AuthRequest(

        @NotBlank
        @Email
        @Size(max = 100)
        @Schema(
                description = "Correo electrónico usado como identificador de usuario",
                example     = "admin@tpagiles.app"
        )
        String mail,

        @NotBlank
        @Size(min = 8, max = 100)
        @Schema(
                description = "Contraseña del usuario",
                example     = "admin123"
        )
        String password

) {
    public AuthRequest {
        mail = mail.trim().toLowerCase();
        // password: se conserva tal cual para permitir espacios o símbolos
    }
}
