package com.tpagiles.app_licencia.service.impl;

import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.Licencia;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.repository.LicenciaRepository;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import com.tpagiles.app_licencia.service.ILicenciaService;
import com.tpagiles.app_licencia.service.ITitularService;
import com.tpagiles.app_licencia.service.helper.CostoLicenciaHelper;
import com.tpagiles.app_licencia.service.helper.LicenciaHelper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LicenciaService implements ILicenciaService {

    private final ITitularService titularService;
    private final LicenciaRepository licenciaRepo;
    private final CostoLicenciaHelper costoHelper;
    private final LicenciaHelper licenciaHelper;

    // Inyectamos el repo de Usuario directamente por ahora despues seria IUsuarioSerie
    private final UsuarioRepository usuarioRepo;

    @Override
    @Transactional
    public LicenciaResponseRecord emitirLicencia(LicenciaRecord req) {
        Titular titular = titularService.obtenerPorId(req.titularId());

        licenciaHelper.validarClaseBasica(req.clase());
        licenciaHelper.validarEdadMinima(titular);
        int vigencia = licenciaHelper.calcularVigencia(titular);

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = licenciaHelper
                .calcularFechaVencimiento(hoy, titular.getFechaNacimiento(), vigencia);

        double costo = costoHelper.obtenerCosto(req.clase(), vigencia);

        // Usamos ahora el repo directamente para buscar al emisor pero despues seria le IUsuarioService en el sprint 2
        Usuario emisor = usuarioRepo
                .findByUsername(req.emisor())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario emisor no encontrado: " + req.emisor())
                );

        Licencia licencia = Licencia.builder()
                .titular(titular)
                .clase(req.clase())
                .vigenciaAnios(vigencia)
                .fechaEmision(hoy)
                .fechaVencimiento(vencimiento)
                .costo(costo)
                .emisor(emisor)
                .vigente(true)
                .build();

        return LicenciaResponseRecord.fromEntity(licenciaRepo.save(licencia));
    }
}




