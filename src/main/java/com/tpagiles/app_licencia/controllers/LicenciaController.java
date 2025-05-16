package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.dto.ErrorResponse;
import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.service.ILicenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Licencia Controller", description = "Operaciones para emisión de licencias")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/licencias")
public class LicenciaController {

    private final ILicenciaService licenciaService;

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
    public ResponseEntity<LicenciaResponseRecord> emitirLicencia(@Valid @RequestBody LicenciaRecord record) {
        LicenciaResponseRecord resp = licenciaService.emitirLicencia(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
