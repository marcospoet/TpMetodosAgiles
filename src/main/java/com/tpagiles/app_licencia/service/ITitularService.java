package com.tpagiles.app_licencia.service;

import com.tpagiles.app_licencia.dto.TitularLicenciaVigenteResponseRecord;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ITitularService {
    Titular createTitular(TitularRecord titular);
    Titular obtenerPorId(Long id);
    List<Titular> listarTodos();
    Titular actualizarTitularPorDocumento(TipoDocumento tipoDocumento,
                                          String numeroDocumento,
                                          TitularRecord titular);
    void eliminarTitular(Long id);

    @Transactional(readOnly = true)
    long contarTitulares();

    @Transactional(readOnly = true)
    Titular obtenerPorTipoYNumeroDocumento(TipoDocumento tipoDocumento,
                                           String numeroDocumento);
    List<TitularLicenciaVigenteResponseRecord> buscarTitularesConLicenciasVigentes(
            String nombreApellido,
            List<String> grupoSanguineo,
            String factorRh,
            Boolean soloDonantes
    );
}
