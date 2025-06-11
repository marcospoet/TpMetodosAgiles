package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.api.TitularApi;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.dto.TitularResponseRecord;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import com.tpagiles.app_licencia.service.ITitularService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class TitularController implements TitularApi {

    private final ITitularService titularService;
    @Override
    public ResponseEntity<TitularResponseRecord> crearTitular(
            @Valid @RequestBody TitularRecord record
    ) {
        var titular = titularService.createTitular(record);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TitularResponseRecord.fromEntity(titular));
    }
    @Override
    public ResponseEntity<TitularResponseRecord> obtenerTitular(@PathVariable Long id) {
        var titular = titularService.obtenerPorId(id);
        return ResponseEntity.ok(TitularResponseRecord.fromEntity(titular));
    }

    @Override
    public ResponseEntity<Long> contarTitulares() {
        return ResponseEntity.ok(titularService.contarTitulares());
    }

    @Override
    public ResponseEntity<TitularResponseRecord> buscarPorTipoYNumeroDocumento(
            @RequestParam TipoDocumento tipoDocumento,
            @RequestParam String numeroDocumento) {

        var titular = titularService.obtenerPorTipoYNumeroDocumento(tipoDocumento, numeroDocumento);
        return ResponseEntity.ok(TitularResponseRecord.fromEntity(titular));
    }
    @Override
    public ResponseEntity<TitularResponseRecord> actualizarTitularPorDocumento(
            @RequestParam TipoDocumento tipoDocumento,
            @RequestParam String numeroDocumento,
            @RequestBody TitularRecord record) {
        var actualizado = titularService.actualizarTitularPorDocumento(tipoDocumento, numeroDocumento, record);
        return ResponseEntity.ok(TitularResponseRecord.fromEntity(actualizado));
    }
}
