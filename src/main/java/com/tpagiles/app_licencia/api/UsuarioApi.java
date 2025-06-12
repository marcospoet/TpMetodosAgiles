package com.tpagiles.app_licencia.api;

import com.tpagiles.app_licencia.dto.ErrorResponse;
import com.tpagiles.app_licencia.dto.UsuarioRecord;
import com.tpagiles.app_licencia.dto.UsuarioResponseRecord;
import com.tpagiles.app_licencia.dto.UsuarioUpdateRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios administrativos del sistema")
@RequestMapping("/api/usuarios")
@SecurityRequirement(name = "bearerAuth")
public interface UsuarioApi {

    @Operation(
            summary = "Crear nuevo usuario",
            description = "Crea un nuevo usuario administrativo. Solo disponible para usuarios con rol SUPER_USER.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuario creado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UsuarioResponseRecord.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos de usuario inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email ya registrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping
    ResponseEntity<UsuarioResponseRecord> crearUsuario(@Valid @RequestBody UsuarioRecord usuarioDTO);

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene la lista de todos los usuarios administrativos. Solo disponible para usuarios con rol SUPER_USER.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de usuarios obtenida correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseRecord.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    ResponseEntity<List<UsuarioResponseRecord>> listarUsuarios();

    @Operation(
            summary = "Modificar usuario existente",
            description = "Actualiza los datos de un usuario por su ID. Solo disponible para usuarios con rol SUPER_USER.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario actualizado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UsuarioResponseRecord.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos de usuario inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email ya registrado por otro usuario",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    ResponseEntity<UsuarioResponseRecord> actualizarUsuario(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UsuarioUpdateRecord usuarioDTO
    );

    @Operation(
            summary = "Eliminar (lógicamente) un usuario",
            description = "Marca el usuario como inactivo. No se elimina físicamente de la base de datos.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Usuario marcado como inactivo correctamente"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> eliminarUsuario(@PathVariable @Positive Long id);

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Obtiene los datos de un usuario por su ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UsuarioResponseRecord.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<UsuarioResponseRecord> obtenerUsuarioPorId(@PathVariable @Positive Long id);

    @Operation(
            summary = "Activar usuario",
            description = "Marca como activo a un usuario previamente inactivo.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario activado correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UsuarioResponseRecord.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PatchMapping("/{id}/activar")
    ResponseEntity<UsuarioResponseRecord> activarUsuario(@PathVariable @Positive Long id);
}