package com.tpagiles.app_licencia.service;

import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;

public interface ILicenciaService {
    LicenciaResponseRecord emitirLicencia(LicenciaRecord request);
}
