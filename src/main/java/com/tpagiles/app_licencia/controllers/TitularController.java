package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.dto.ErrorResponse;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.dto.TitularResponseRecord;
import com.tpagiles.app_licencia.service.ITitularService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


@Tag(name = "Titulares", description = "API para gestión de titulares")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/titulares")
public class TitularController {

    private final ITitularService titularService;

    @Operation(
            summary     = "Crear un nuevo Titular",
            description = "Valida y persiste un Titular. Devuelve el recurso creado con su ID.",
            tags        = { "Titulares" },
            requestBody = @RequestBody(
                    description = "Datos del Titular a crear",
                    required    = true,
                    content     = @Content(
                            mediaType = "application/json",
                            schema    = @Schema(implementation = TitularRecord.class),
                            examples  = @ExampleObject(
                                    name  = "NuevoTitular",
                                    value = """
                        {
                          "nombre": "Ana",
                          "apellido": "García",
                          "fechaNacimiento": "1985-05-20",
                          "tipoDocumento": "DNI",
                          "numeroDocumento": "87654321",
                          "grupoSanguineo": "AB",
                          "factorRh": "NEGATIVO",
                          "direccion": "Av. Siempre Viva 742",
                          "donanteOrganos": false
                        }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description  = "Titular creado exitosamente",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = TitularResponseRecord.class),
                                    examples  = @ExampleObject(
                                            name  = "CreatedResponse",
                                            value = """
                            {
                              "id": 100,
                              "nombre": "Ana",
                              "apellido": "García",
                              "fechaNacimiento": "1985-05-20",
                              "tipoDocumento": "DNI",
                              "numeroDocumento": "87654321",
                              "grupoSanguineo": "AB",
                              "factorRh": "NEGATIVO",
                              "direccion": "Av. Siempre Viva 742",
                              "donanteOrganos": false
                            }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description  = "Datos inválidos",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "BadRequest",
                                            value = """
                            {
                              "timestamp": "2025-05-16T14:00:00Z",
                              "status": 400,
                              "message": "El nombre no puede estar vacío"
                            }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description  = "Duplicado (mismo documento)",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "Conflict",
                                            value = """
                            {
                              "timestamp": "2025-05-16T14:01:00Z",
                              "status": 409,
                              "message": "Ya existe un Titular con documento: 87654321"
                            }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description  = "Error interno del servidor",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "ServerError",
                                            value = """
                            {
                              "timestamp": "2025-05-16T14:02:00Z",
                              "status": 500,
                              "message": "Error interno del servidor"
                            }"""
                                    )
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<TitularResponseRecord> crearTitular(
            @Valid @RequestBody TitularRecord record
    ) {
        var titular = titularService.createTitular(record);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TitularResponseRecord.fromEntity(titular));
    }

    @Operation(
            summary     = "Obtener Titular por ID",
            description = "Recupera los datos de un Titular existente a partir de su identificador único.",
            tags        = { "Titulares" },
            parameters  = {
                    @Parameter(
                            name        = "id",
                            description = "ID del Titular a recuperar",
                            required    = true,
                            in          = ParameterIn.PATH,
                            example     = "100"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description  = "Titular encontrado",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = TitularResponseRecord.class),
                                    examples  = @ExampleObject(
                                            name  = "GetResponse",
                                            value = """
                            {
                              "id": 100,
                              "nombre": "Ana",
                              "apellido": "García",
                              "fechaNacimiento": "1985-05-20",
                              "tipoDocumento": "DNI",
                              "numeroDocumento": "87654321",
                              "grupoSanguineo": "AB",
                              "factorRh": "NEGATIVO",
                              "direccion": "Av. Siempre Viva 742",
                              "donanteOrganos": false
                            }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description  = "Titular no encontrado",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "NotFound",
                                            value = """
                            {
                              "timestamp": "2025-05-16T14:03:00Z",
                              "status": 404,
                              "message": "No se encontró el Titular con ID: 100"
                            }"""
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TitularResponseRecord> obtenerTitular(@PathVariable Long id) {
        var titular = titularService.obtenerPorId(id);
        return ResponseEntity.ok(TitularResponseRecord.fromEntity(titular));
    }
}
