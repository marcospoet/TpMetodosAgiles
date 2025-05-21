package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.api.TitularApi;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.dto.TitularResponseRecord;
import com.tpagiles.app_licencia.service.ITitularService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class TitularController implements TitularApi {

    private final ITitularService titularService;

    @PostMapping
    public ResponseEntity<TitularResponseRecord> crearTitular(
            @Valid @RequestBody TitularRecord record
    ) {
        var titular = titularService.createTitular(record);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TitularResponseRecord.fromEntity(titular));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TitularResponseRecord> obtenerTitular(@PathVariable Long id) {
        var titular = titularService.obtenerPorId(id);
        return ResponseEntity.ok(TitularResponseRecord.fromEntity(titular));
    }
}
