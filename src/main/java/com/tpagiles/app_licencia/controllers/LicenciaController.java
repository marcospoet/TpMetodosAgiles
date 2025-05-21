package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.api.LicenciaApi;
import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.service.ILicenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LicenciaController implements LicenciaApi {

    private final ILicenciaService licenciaService;

    @PostMapping
    public ResponseEntity<LicenciaResponseRecord> emitirLicencia(@Valid @RequestBody LicenciaRecord record) {
        LicenciaResponseRecord resp = licenciaService.emitirLicencia(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
