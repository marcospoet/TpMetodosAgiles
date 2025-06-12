package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.api.LicenciaApi;
import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.dto.RenovarLicenciaRequest;
import com.tpagiles.app_licencia.dto.TitularConLicenciasResponseRecord;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import com.tpagiles.app_licencia.service.ILicenciaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LicenciaController implements LicenciaApi {

    private final ILicenciaService licenciaService;

    @Override
    public ResponseEntity<LicenciaResponseRecord> emitirLicencia(@Valid @RequestBody LicenciaRecord record) {
        LicenciaResponseRecord resp = licenciaService.emitirLicencia(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
    @Override
    public ResponseEntity<List<LicenciaResponseRecord>> listarVencidas() {
        List<LicenciaResponseRecord> list = licenciaService.listarLicenciasVencidas();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<Long> contarLicenciasVencidas() {
        return ResponseEntity.ok(licenciaService.contarLicenciasVencidas());
    }

    @Override
    public ResponseEntity<Long> contarTotalLicenciasEmitidas() {
        return ResponseEntity.ok(licenciaService.contarTotalLicenciasEmitidas());
    }

    @Override
    public ResponseEntity<TitularConLicenciasResponseRecord> buscarPorTipoYNumeroDocumento(
            @RequestParam @NotNull TipoDocumento tipoDocumento,
            @RequestParam @NotBlank String numeroDocumento
    ) {
        TitularConLicenciasResponseRecord dto =
                licenciaService.buscarPorTipoYNumeroDocumento(tipoDocumento, numeroDocumento);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<LicenciaResponseRecord> renovarLicencia(@Valid @RequestBody RenovarLicenciaRequest request) {
        System.out.println("Request - Numero copia: " + request.numeroCopia());
        System.out.println("Request - Motivo copia: " + request.motivoCopia());
        System.out.println("Request - Licencia original ID: " + request.licenciaOriginalId());

        LicenciaResponseRecord licenciaRenovada = licenciaService.renovarLicencia(request);
        return ResponseEntity.ok(licenciaRenovada);
    }
}
