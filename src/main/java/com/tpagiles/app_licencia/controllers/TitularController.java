package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.dto.TitularResponseRecord;
import com.tpagiles.app_licencia.dto.ErrorResponse;
import com.tpagiles.app_licencia.service.ITitularService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Titular Controller", description = "Operaciones para la gestión de titulares")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/titulares")
public class TitularController {

    private final ITitularService titularService;

    @Operation(
            summary     = "Crear titular",
            description = "Valida y da de alta un nuevo Titular",
            responses   = {
                    @ApiResponse(responseCode = "201", description = "Licencia emitida exitosamente"),
                    @ApiResponse(
                            responseCode = "400",
                            description  = "Solicitud inválida",
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
                            description  = "DNI duplicado",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "Conflict",
                                            value = """
                                                    {
                                                      "timestamp": "2025-05-16T14:01:00Z",
                                                      "status": 409,
                                                      "message": "Ya existe un Titular con documento: 12345678"
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
        var resp    = TitularResponseRecord.fromEntity(titular);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
