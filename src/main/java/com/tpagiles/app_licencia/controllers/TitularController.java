package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.dto.TitularResponseRecord;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.service.ITitularService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            responses = {
                    @ApiResponse(responseCode = "201", description = "Titular creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "409", description = "DNI duplicado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping
    public ResponseEntity<TitularResponseRecord> crearTitular(@Valid @RequestBody TitularRecord record) {
        Titular titular= titularService.createTitular(record);
        TitularResponseRecord resp = TitularResponseRecord.fromEntity(titular);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
