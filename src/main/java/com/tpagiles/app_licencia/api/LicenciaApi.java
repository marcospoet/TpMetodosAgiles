package com.tpagiles.app_licencia.api;

import com.tpagiles.app_licencia.dto.*;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Licencias", description = "Operaciones para emisión de licencias")
@SecurityRequirement(name = "bearerAuth")      // <<–– obliga a enviar JWT Bearer
@RequestMapping("/api/licencias")
@Validated
public interface LicenciaApi {

    @Operation(
            summary     = "Emitir licencia (OPERADOR, SUPER_USER)",
            description = "Emite una licencia de clase A o B para un titular existente. Se debe indicar el mail del emisor. Requiere rol SUPER_USER o OPERADOR.",
            security    = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Licencia emitida exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida o regla de negocio incumplida",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "BadRequest",
                                            value = """
                        {
                          "timestamp": "2025-05-15T10:00:00",
                          "status": 400,
                          "message": "Clase debe ser A o B"
                        }"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Titular o emisor no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "NotFound",
                                            value = """
                        {
                          "timestamp": "2025-05-15T10:05:00",
                          "status": 404,
                          "message": "Titular con id 37 no existe"
                        }"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "ServerError",
                                            value = """
                        {
                          "timestamp": "2025-05-15T10:10:00",
                          "status": 500,
                          "message": "NullPointerException en LicenciaService"
                        }"""
                                    )
                            )
                    )
            }
    )
    @PostMapping
    ResponseEntity<LicenciaResponseRecord> emitirLicencia(
            @Valid @RequestBody LicenciaRecord record
    );

    @Operation(
            summary     = "Listar licencias vencidas (OPERADOR, SUPER_USER)",
            description = "Recupera todas las licencias cuya fecha de vencimiento sea anterior al día actual. Requiere rol OPERADOR o SUPER_USER.",
            security    = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listado de licencias vencidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    array   = @ArraySchema(schema = @Schema(implementation = LicenciaResponseRecord.class)),
                                    examples = @ExampleObject(
                                            name  = "LicenciasVencidas",
                                            value = """
                        [
                          { "id":123, "titularId":37, "clase":"A", "vigenciaAnios":5, "fechaEmision":"2020-05-16", "fechaVencimiento":"2025-05-16", "costo":120.50, "emisor":"juan.perez", "vigente":false },
                          { "id":124, "titularId":42, "clase":"B", "vigenciaAnios":3, "fechaEmision":"2022-01-10", "fechaVencimiento":"2025-01-10", "costo":100.00, "emisor":"maria.lopez", "vigente":false }
                        ]"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden: rol insuficiente",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/vencidas")
    ResponseEntity<List<LicenciaResponseRecord>> listarVencidas();

    @Operation(
            summary     = "Contar licencias vencidas (OPERADOR, SUPER_USER)",
            description = "Devuelve la cantidad total de licencias que han vencido. Requiere rol OPERADOR o SUPER_USER.",
            security    = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cantidad de licencias vencidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema   = @Schema(type = "integer", format = "int64"),
                                    examples = @ExampleObject(name = "CountVencidas", value = "2")
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden: rol insuficiente",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/vencidas/count")
    ResponseEntity<Long> contarLicenciasVencidas();

    @Operation(
            summary     = "Contar total de licencias emitidas (OPERADOR, SUPER_USER)",
            description = "Devuelve el total de licencias emitidas. Requiere rol OPERADOR o SUPER_USER.",
            security    = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total de licencias emitidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema   = @Schema(type = "integer", format = "int64"),
                                    examples = @ExampleObject(name = "CountEmitidas", value = "150")
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden: rol insuficiente",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/emitidas/count")
    ResponseEntity<Long> contarTotalLicenciasEmitidas();

    @Operation(
            summary     = "Obtener un titular con al menos una licencia (OPERADOR, SUPER_USER)",
            description = """
            Recupera los datos del titular y su lista completa de licencias
            filtrando por tipo y número de documento. Requiere rol OPERADOR o SUPER_USER.
            Solo muestra titulares que tengan al menos una licencia, si no tiene ninguna devolverá 404.
            El parámetro `tipoDocumento` no puede ser nulo y `numeroDocumento` no puede estar vacío.
            """,
            security    = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name        = "tipoDocumento",
                            in          = ParameterIn.QUERY,
                            description = "Tipo de documento del titular (DNI, LC, PASAPORTE...)",
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
                    @ApiResponse(responseCode = "200", description = "Titular y sus licencias encontrados",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = TitularConLicenciasResponseRecord.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos (@NotNull o @NotBlank)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "BadRequestParams",
                                            value = """
                        {
                          "timestamp": "2025-05-22T12:00:00Z",
                          "status": 400,
                          "message": "tipoDocumento: must not be null; numeroDocumento: must not be blank"
                        }"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Titular no encontrado",
                            content = @Content(
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
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden: rol insuficiente",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/titular")
    ResponseEntity<TitularConLicenciasResponseRecord> buscarPorTipoYNumeroDocumento(
            @RequestParam @NotNull TipoDocumento tipoDocumento,
            @RequestParam @NotBlank String numeroDocumento
    );
    @Operation(
            summary     = "Renovar licencia (OPERADOR, SUPER_USER)",
            description = """
            Renueva una licencia existente por motivo de vencimiento o cambio de datos del titular.
            Para licencias vencidas, se crea una nueva licencia con fechas actualizadas.
            Para cambio de datos, se actualizan los datos del titular y se emite nueva licencia.
            La licencia anterior queda inactiva. Requiere rol OPERADOR o SUPER_USER.
            """,
            security    = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @RequestBody(
                    description = "Datos para renovar la licencia",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RenovarLicenciaRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "RenovacionPorVencimiento",
                                            summary = "Renovar licencia vencida",
                                            value = """
                                                {
                                                      "licenciaId": 123,
                                                      "motivoRenovacion": "VENCIDA",
                                                      "numeroCopia": 1,
                                                      "motivoCopia": "Primera copia por renovación",
                                                      "licenciaOriginalId": 1
                                                 }"""
                                    ),
                                    @ExampleObject(
                                            name = "RenovacionPorCambioDatos",
                                            summary = "Renovar por cambio de datos",
                                            value = """
                                                {
                                                        "licenciaId": 124,
                                                        "motivoRenovacion": "CAMBIO_DATOS",
                                                        "nuevoNombre": "Juan Carlos",
                                                        "nuevaDireccion": "Av. Libertador 1234",
                                                        "numeroCopia": 2,
                                                        "motivoCopia": "Copia por cambio de datos",
                                                        "licenciaOriginalId": 1
                                                 }"""
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Licencia renovada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LicenciaResponseRecord.class),
                                    examples = @ExampleObject(
                                            name = "LicenciaRenovada",
                                            value = """
                                    {
                                      "id": 156,
                                      "titularId": 37,
                                      "clase": "A",
                                      "vigenciaAnios": 5,
                                      "fechaEmision": "2025-06-11",
                                      "fechaVencimiento": "2030-06-11",
                                      "costo": 120.50,
                                      "emisor": "sistema.renovacion",
                                      "vigente": true
                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida o regla de negocio incumplida",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "LicenciaNoVencida",
                                                    value = """
                                            {
                                              "timestamp": "2025-06-11T10:00:00",
                                              "status": 400,
                                              "message": "La licencia aún no está vencida"
                                            }"""
                                            ),
                                            @ExampleObject(
                                                    name = "MotivoInvalido",
                                                    value = """
                                            {
                                              "timestamp": "2025-06-11T10:00:00",
                                              "status": 400,
                                              "message": "Motivo de renovación no válido: OTRO"
                                            }"""
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Licencia no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "LicenciaNotFound",
                                            value = """
                                    {
                                      "timestamp": "2025-06-11T10:05:00",
                                      "status": 404,
                                      "message": "Licencia no encontrada con ID: 999"
                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden: rol insuficiente",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "ServerError",
                                            value = """
                                    {
                                      "timestamp": "2025-06-11T10:10:00",
                                      "status": 500,
                                      "message": "Error interno al procesar renovación"
                                    }"""
                                    )
                            )
                    )
            }
    )
    @PostMapping("/renovar")
    ResponseEntity<LicenciaResponseRecord> renovarLicencia(
            @Valid @RequestBody RenovarLicenciaRequest request
    );
    @Operation(
            summary = "Emitir copia de licencia (OPERADOR, SUPER_USER)",
            description = "Emite una nueva copia de una licencia existente (por pérdida, robo, deterioro). Requiere rol OPERADOR o SUPER_USER.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @RequestBody(
                    description = "Datos necesarios para emitir la copia",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmitirCopiaRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Copia emitida exitosamente",
                            content = @Content(schema = @Schema(implementation = LicenciaResponseRecord.class))),
                    @ApiResponse(responseCode = "400", description = "La licencia está vencida o motivo inválido",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Licencia original o emisor no encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/copias")
    ResponseEntity<LicenciaResponseRecord> emitirCopia(@Valid @RequestBody EmitirCopiaRequest request);

}
