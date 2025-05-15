package com.tpagiles.app_licencia.service;

import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.Licencia;

import java.util.List;
import java.util.Optional;
public interface ILicenciaService {
    public LicenciaResponseRecord emitirLicencia(LicenciaRecord request);
    public int calcularVigencia(int edad, Titular titular);
}
