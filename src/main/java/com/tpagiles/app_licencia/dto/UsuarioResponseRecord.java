package com.tpagiles.app_licencia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.model.enums.Rol;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "nombre",
        "apellido",
        "mail",
        "roles",
        "activo"
})
@Schema(description = "Datos de un usuario del sistema")
public record UsuarioResponseRecord(

        @Schema(
                description = "ID del usuario",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long id,

        @Schema(
                description = "Nombre del usuario",
                example = "María",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String nombre,

        @Schema(
                description = "Apellido del usuario",
                example = "González",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String apellido,

        @Schema(
                description = "Correo electrónico del usuario",
                example = "maria.gonzalez@municipio.gob",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String mail,

        @Schema(
                description = "Roles asignados al usuario",
                example = "[\"OPERADOR\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Set<Rol> roles,

        @Schema(
                description = "Estado del usuario (true = activo, false = inactivo)",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        boolean activo
) {
    public static UsuarioResponseRecord fromUsuario(Usuario usuario) {
        return new UsuarioResponseRecord(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getMail(),
                usuario.getRoles(),
                usuario.isActivo()
        );
    }
}