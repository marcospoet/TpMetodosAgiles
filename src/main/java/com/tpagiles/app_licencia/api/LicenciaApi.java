package com.tpagiles.app_licencia.api;

import com.tpagiles.app_licencia.dto.ErrorResponse;
import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.dto.TitularConLicenciasResponseRecord;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Licencias", description = "Operaciones para emisión de licencias")
@RequestMapping("/api/licencias")
@Validated
public interface LicenciaApi {

    @Operation(
            summary     = "Emitir licencia",
            description = "Emite una licencia de clase A o B para un titular existente. Se debe indicar el emisor.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Licencia emitida exitosamente"),
                    @ApiResponse(
                            responseCode = "400",
                            description  = "Solicitud inválida o regla de negocio incumplida",
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
                    @ApiResponse(
                            responseCode = "404",
                            description  = "Titular o emisor no encontrado",
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
                    @ApiResponse(
                            responseCode = "500",
                            description  = "Error interno del servidor",
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
            summary     = "Listar licencias vencidas",
            description = "Recupera todas las licencias cuya fecha de vencimiento sea anterior al día actual.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listado de licencias vencidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    array   = @ArraySchema(schema = @Schema(implementation = LicenciaResponseRecord.class)),
                                    examples = @ExampleObject(
                                            name  = "LicenciasVencidas",
                                            value = """
                        [
                          {
                            "id": 123,
                            "titularId": 37,
                            "clase": "A",
                            "vigenciaAnios": 5,
                            "fechaEmision": "2020-05-16",
                            "fechaVencimiento": "2025-05-16",
                            "costo": 120.50,
                            "emisor": "juan.perez",
                            "vigente": false
                          },
                          {
                            "id": 124,
                            "titularId": 42,
                            "clase": "B",
                            "vigenciaAnios": 3,
                            "fechaEmision": "2022-01-10",
                            "fechaVencimiento": "2025-01-10",
                            "costo": 100.00,
                            "emisor": "maria.lopez",
                            "vigente": false
                          }
                        ]"""
                                    )
                            )
                    )
            }
    )
    @GetMapping("/vencidas")
    ResponseEntity<List<LicenciaResponseRecord>> listarVencidas();

    @Operation(
            summary     = "Contar licencias vencidas",
            description = "Devuelve la cantidad total de licencias que han vencido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cantidad de licencias vencidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema   = @Schema(type = "integer", format = "int64"),
                                    examples = @ExampleObject(
                                            name  = "CountVencidas",
                                            value = "2"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/vencidas/count")
    ResponseEntity<Long> contarLicenciasVencidas();

    @Operation(
            summary     = "Contar total de licencias emitidas",
            description = "Devuelve el total de licencias emitidas en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total de licencias emitidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema   = @Schema(type = "integer", format = "int64"),
                                    examples = @ExampleObject(
                                            name  = "CountEmitidas",
                                            value = "150"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/emitidas/count")
    ResponseEntity<Long> contarTotalLicenciasEmitidas();
    // LicenciaApi.java (documentación para el nuevo endpoint)
    @Operation(
            summary     = "Obtener titular con sus licencias",
            description = "Recupera los datos del titular y su lista completa de licencias filtrando por tipo y número de documento. " +
                    "El parámetro `tipoDocumento` no puede ser nulo y `numeroDocumento` no puede estar vacío.",
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
                    @ApiResponse(
                            responseCode = "200",
                            description  = "Titular y sus licencias encontrados",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = TitularConLicenciasResponseRecord.class),
                                    examples  = @ExampleObject(
                                            name  = "TitularConLicencias",
                                            value = """
                    {
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
                      },
                      "licencias": [
                        {
                          "id": 101,
                          "titularId": 1,
                          "clase": "A",
                          "vigenciaAnios": 5,
                          "fechaEmision": "2021-06-10",
                          "fechaVencimiento": "2026-06-10",
                          "costo": 120.00,
                          "emisor": "jose.alfaro",
                          "vigente": true
                        },
                        {
                          "id": 102,
                          "titularId": 1,
                          "clase": "B",
                          "vigenciaAnios": 3,
                          "fechaEmision": "2020-04-15",
                          "fechaVencimiento": "2023-04-15",
                          "costo": 100.00,
                          "emisor": "maria.lopez",
                          "vigente": false
                        }
                      ]
                    }"""
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
    @GetMapping("/titular")
    ResponseEntity<TitularConLicenciasResponseRecord> buscarPorTipoYNumeroDocumento(
            @RequestParam @NotNull TipoDocumento tipoDocumento,
            @RequestParam @NotBlank String numeroDocumento
    );

}
