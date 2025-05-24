package com.tpagiles.app_licencia.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para el registro de un nuevo usuario.
 */
@Schema(name = "RegisterRequest", description = "Datos necesarios para registrar un usuario con rol OPERADOR")
public record RegisterRequest(

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Nombre del usuario", example = "Marcos")
        String nombre,

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Apellido del usuario", example = "Poet")
        String apellido,

        @NotBlank
        @Email
        @Size(max = 100)
        @Schema(description = "Correo electrónico (será usado como username)", example = "marcos@tp.com")
        String mail,

        @NotBlank
        @Size(min = 8, max = 100)
        @Schema(description = "Contraseña (mínimo 8 caracteres)", example = "miPass123")
        String password

) {
    /**
     * Normaliza y valida datos al construir el record.
     */
    public RegisterRequest {
        nombre = nombre.trim();
        apellido = apellido.trim();
        mail = mail.trim().toLowerCase();
        // password lo dejamos tal cual (por si necesita caracteres especiales)
    }
}
