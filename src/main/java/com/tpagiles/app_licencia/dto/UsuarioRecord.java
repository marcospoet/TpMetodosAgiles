package com.tpagiles.app_licencia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.model.enums.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "nombre",
        "apellido",
        "mail",
        "password",
        "roles"
})
@Schema(description = "Datos necesarios para dar de alta o modificar un Usuario")
public record UsuarioRecord(

        @Schema(
                description = "Nombre del usuario",
                example = "María",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
        String nombre,

        @Schema(
                description = "Apellido del usuario",
                example = "González",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El apellido no puede estar vacío")
        @Size(max = 50, message = "El apellido no puede superar 50 caracteres")
        String apellido,

        @Schema(
                description = "Correo electrónico del usuario",
                example = "maria.gonzalez@municipio.gob",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El correo electrónico no puede estar vacío")
        @Email(message = "Debe ser un correo electrónico válido")
        @Size(max = 100, message = "El correo electrónico no puede superar 100 caracteres")
        String mail,

        @Schema(
                description = "Contraseña del usuario",
                example = "Contraseña123!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, max = 30, message = "La contraseña debe tener entre 8 y 30 caracteres")
        String password,

        @Schema(
                description = "Roles asignados al usuario",
                    example = "[\"OPERADOR\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Debe asignar al menos un rol")
        Set<Rol> roles
) {
    public Usuario toUsuario() {
        Usuario u = new Usuario();
        u.setNombre(this.nombre);
        u.setApellido(this.apellido);
        u.setMail(this.mail);
        u.setPassword(this.password);
        u.setRoles(this.roles);
        return u;
    }
}