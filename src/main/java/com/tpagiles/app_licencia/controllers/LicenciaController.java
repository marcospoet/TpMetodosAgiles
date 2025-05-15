package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.service.impl.LicenciaService;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.dto.LicenciaRecord;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/licencias")
@RequiredArgsConstructor
public class LicenciaController {

    private final LicenciaService licenciaService;

    @PostMapping
    public ResponseEntity<LicenciaResponseRecord> emitirLicencia(@Valid @RequestBody LicenciaRecord request) {
        LicenciaResponseRecord response = licenciaService.emitirLicencia(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
