package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.dto.TitularResponseRecord;
import com.tpagiles.app_licencia.dto.ErrorResponse;
import com.tpagiles.app_licencia.service.ITitularService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Titulares", description = "Operaciones sobre titulares")
@RestController
@RequestMapping("/api/titulares")
@RequiredArgsConstructor
public class TitularController {

    private final ITitularService titularService;

    @Operation(summary = "Crear titular")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Titular creado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TitularResponseRecord.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(ref = "BadRequestExample")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "DNI duplicado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(ref = "ConflictExample")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<TitularResponseRecord> crearTitular(
            @Valid @RequestBody TitularRecord record) {
        var titular = titularService.createTitular(record);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TitularResponseRecord.fromEntity(titular));
    }

    @Operation(summary = "Obtener titular por ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Titular encontrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TitularResponseRecord.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Titular no encontrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(ref = "NotFoundExample")
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TitularResponseRecord> obtenerTitular(@PathVariable Long id) {
        var titular = titularService.obtenerPorId(id);
        return ResponseEntity.ok(TitularResponseRecord.fromEntity(titular));
    }
}

