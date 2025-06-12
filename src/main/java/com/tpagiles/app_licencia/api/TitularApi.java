package com.tpagiles.app_licencia.api;

import com.tpagiles.app_licencia.dto.*;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Titulares", description = "API para gestión de titulares")
@SecurityRequirement(name = "bearerAuth")    // ← aquí
@RequestMapping("/api/titulares")
@Validated
public interface TitularApi {

    @Operation(
            summary     = "Crear un nuevo Titular",
            description = "Valida y persiste un Titular. Devuelve el recurso creado con su ID.",
            tags        = { "Titulares" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
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
    ResponseEntity<TitularResponseRecord> crearTitular(
            @Valid @RequestBody TitularRecord record
    );

    @Operation(
            summary     = "Obtener Titular por ID",
            description = "Recupera los datos de un Titular existente a partir de su identificador único. " +
                    "El parámetro `id` debe ser un número entero positivo.",
            parameters  = {
                    @Parameter(
                            name        = "id",
                            in          = ParameterIn.PATH,
                            description = "ID del Titular a recuperar (debe ser ≥ 1)",
                            required    = true,
                            schema      = @Schema(type = "integer", format = "int64", minimum = "1"),
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
                            responseCode = "400",
                            description  = "ID inválido: violación de @Positive",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "BadRequest",
                                            value = """
                    {
                      "timestamp": "2025-05-21T17:30:00Z",
                      "status": 400,
                      "message": "obtenerTitular.id: must be greater than 0"
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
                      "timestamp": "2025-05-21T17:31:00Z",
                      "status": 404,
                      "message": "No se encontró el Titular con ID: 100"
                    }"""
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<TitularResponseRecord> obtenerTitular(
            @PathVariable @Positive Long id
    );

    @Operation(
            summary     = "Contar titulares registrados",
            description = "Devuelve el número total de titulares registrados en el sistema.",
            tags        = { "Titulares" },
            responses   = {
                    @ApiResponse(
                            responseCode = "200",
                            description  = "Conteo exitoso",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(type = "integer", format = "int64", example = "42")
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
                                              "timestamp": "2025-05-22T14:00:00Z",
                                              "status": 500,
                                              "message": "Error interno del servidor"
                                            }"""
                                    )
                            )
                    )
            }
    )
    @GetMapping("/count")
    ResponseEntity<Long> contarTitulares();


    @Operation(
            summary     = "Obtener titular con sus licencias",
            description = "Recupera los datos del titular"
                    + "por tipo y número de documento. "
                    + "El parámetro `tipoDocumento` no puede ser nulo y `numeroDocumento` no puede estar vacío.",
            parameters = {
                    @Parameter(
                            name        = "tipoDocumento",
                            in          = ParameterIn.QUERY,
                            description = "Tipo de documento del titular (DNI, LC, PASAPORTE, …)",
                            required    = true,
                            schema      = @Schema(implementation = TipoDocumento.class),
                            example     = "DNI"
                    ),
                    @Parameter(
                            name        = "numeroDocumento",
                            in          = ParameterIn.QUERY,
                            description = "Número de documento del titular",
                            required    = true,
                            schema      = @Schema(type = "string"),
                            example     = "12345678"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description  = "Titular y sus licencias encontrados",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = TitularConLicenciasResponseRecord.class),
                                    examples  = @ExampleObject(
                                            name  = "TitularConLicencias",
                                            value = """                             
                                      "titular": {
                                        "id": 1,
                                        "nombre": "Juan",
                                        "apellido": "Pérez",
                                        "fechaNacimiento": "1990-05-15",
                                        "tipoDocumento": "DNI",
                                        "numeroDocumento": "12345678",
                                        "grupoSanguineo": "O",
                                        "factorRh": "POSITIVO",
                                        "direccion": "Av. Siempre Viva 742",
                                        "donanteOrganos": true
                                      }
                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description  = "Parámetros inválidos (@NotNull o @NotBlank)",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "BadRequestParams",
                                            value = """
                                            {
                                              "timestamp": "2025-05-22T12:00:00Z",
                                              "status": 400,
                                              "message": "buscarPorTipoYNumeroDocumento.tipoDocumento: must not be null; numeroDocumento: must not be blank"
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
                                            name  = "NotFoundTitular",
                                            value = """
                                            {
                                              "timestamp": "2025-05-22T12:01:00Z",
                                              "status": 404,
                                              "message": "No se encontró ningún titular con DNI 12345678"
                                            }"""
                                    )
                            )
                    )
            }
    )
    @GetMapping(params = { "tipoDocumento", "numeroDocumento" })
    ResponseEntity<TitularResponseRecord> buscarPorTipoYNumeroDocumento(
            @RequestParam @NotNull TipoDocumento tipoDocumento,
            @RequestParam @NotBlank String numeroDocumento
    );

    @Operation(
            summary = "Modificar un Titular por tipo y número de documento",
            description = "Actualiza los datos de un titular identificándolo por su tipo y número de documento.",
            parameters = {
                    @Parameter(
                            name = "tipoDocumento",
                            in = ParameterIn.QUERY,
                            description = "Tipo de documento del titular a modificar",
                            required = true,
                            schema = @Schema(implementation = TipoDocumento.class),
                            example = "DNI"
                    ),
                    @Parameter(
                            name = "numeroDocumento",
                            in = ParameterIn.QUERY,
                            description = "Número de documento del titular a modificar",
                            required = true,
                            schema = @Schema(type = "string"),
                            example = "12345678"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del titular",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TitularRecord.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Titular actualizado correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TitularResponseRecord.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Titular no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "El nuevo número de documento ya existe",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PutMapping(params = { "tipoDocumento", "numeroDocumento" })
    ResponseEntity<TitularResponseRecord> actualizarTitularPorDocumento(
            @RequestParam @NotNull TipoDocumento tipoDocumento,
            @RequestParam @NotBlank String numeroDocumento,
            @Valid @RequestBody TitularRecord record
    );

    @Operation(
            summary = "Listar titulares con licencias vigentes y filtros",
            description = "Devuelve titulares que tienen al menos una licencia vigente. Se pueden aplicar filtros opcionales.",
            parameters = {
                    @Parameter(name = "nombreApellido", description = "Filtro parcial por nombre o apellido", example = "Pérez"),
                    @Parameter(name = "grupoSanguineo", description = "Filtro por grupo sanguíneo (puede ser múltiple)", example = "A,B"),
                    @Parameter(name = "factorRh", description = "Filtro por factor RH", example = "POSITIVO"),
                    @Parameter(name = "soloDonantes", description = "Si es true, devuelve solo donantes", example = "true")
            }
    )
    @GetMapping("/licencias-vigentes")
    ResponseEntity<List<TitularLicenciaVigenteResponseRecord>> listarTitularesConLicenciasVigentes(
            @RequestParam(required = false) String nombreApellido,
            @RequestParam(required = false) List<String> grupoSanguineo,
            @RequestParam(required = false) String factorRh,
            @RequestParam(required = false) Boolean soloDonantes
    );


}
