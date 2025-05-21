package com.tpagiles.app_licencia.service;

import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ILicenciaService {
    LicenciaResponseRecord emitirLicencia(LicenciaRecord request);

    @Transactional(readOnly = true)
    List<LicenciaResponseRecord> listarLicenciasVencidas();

    @Transactional(readOnly = true)
    long contarLicenciasVencidas();

    @Transactional(readOnly = true)
    long contarTotalLicenciasEmitidas();
}
