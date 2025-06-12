package com.tpagiles.app_licencia.service.impl;

import com.tpagiles.app_licencia.dto.TitularLicenciaVigenteResponseRecord;
import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.exception.ResourceAlreadyExistsException;
import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import com.tpagiles.app_licencia.repository.TitularRepository;
import com.tpagiles.app_licencia.service.ITitularService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor //generate a private constructor for us that contains constants and variables decorated with the final keyword.
public class TitularService implements ITitularService{
    private final TitularRepository titularRepository;

    @Override
    public Titular createTitular(TitularRecord record) {
        if (titularRepository.existsByNumeroDocumento(record.numeroDocumento())) {
            throw new ResourceAlreadyExistsException(
                    "Ya existe un Titular con documento: " + record.numeroDocumento());
        }
        Titular titular = record.toTitular();
        return titularRepository.save(titular);

    }

    @Override
    @Transactional(readOnly = true)
    public Titular obtenerPorId(Long id) {
        return titularRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Titular no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Titular> listarTodos() {
        return titularRepository.findAll();
    }

    @Override
    public Titular actualizarTitularPorDocumento(TipoDocumento tipoDocumento,
                                                 String numeroDocumento,
                                                 TitularRecord updated) {
        Titular existente = titularRepository
                .findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Titular no encontrado con " + tipoDocumento + " Nº: " + numeroDocumento));

        // Verificar si el nuevo número de documento ya existe en otro titular
        if (!existente.getNumeroDocumento().equals(updated.numeroDocumento()) &&
                titularRepository.existsByNumeroDocumento(updated.numeroDocumento())) {
            throw new ResourceAlreadyExistsException(
                    "Ya existe otro Titular con documento: " + updated.numeroDocumento());
        }

        existente.setNombre(updated.nombre());
        existente.setApellido(updated.apellido());
        existente.setFechaNacimiento(updated.fechaNacimiento());
        existente.setTipoDocumento(updated.tipoDocumento());
        existente.setNumeroDocumento(updated.numeroDocumento());
        existente.setGrupoSanguineo(updated.grupoSanguineo());
        existente.setFactorRh(updated.factorRh());
        existente.setDireccion(updated.direccion());
        existente.setDonanteOrganos(updated.donanteOrganos());

        return titularRepository.save(existente);
    }

    @Override
    public void eliminarTitular(Long id) {
        if (!titularRepository.existsById(id)) {
            throw new ResourceNotFoundException("Titular no encontrado con id: " + id);
        }
        titularRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public long contarTitulares() {
        return titularRepository.count();
    }

    @Transactional(readOnly = true)
    @Override
    public Titular obtenerPorTipoYNumeroDocumento(TipoDocumento tipoDocumento,
                                                  String numeroDocumento) {
        return titularRepository
                .findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Titular no encontrado con " + tipoDocumento + " Nº: " + numeroDocumento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TitularLicenciaVigenteResponseRecord> buscarTitularesConLicenciasVigentes(
            String nombreApellido,
            List<String> grupoSanguineoParam,
            String factorRhParam,
            Boolean soloDonantes) {

        // Convertir List<String> → List<GrupoSanguineo>
        List<GrupoSanguineo> grupoSanguineo = null;
        if (grupoSanguineoParam != null && !grupoSanguineoParam.isEmpty()) {
            grupoSanguineo = grupoSanguineoParam.stream()
                    .map(s -> GrupoSanguineo.valueOf(s.trim().toUpperCase()))
                    .toList();
        }

        // Convertir String → FactorRh
        FactorRh factorRh = null;
        if (factorRhParam != null && !factorRhParam.isBlank()) {
            factorRh = FactorRh.valueOf(factorRhParam.trim().toUpperCase());
        }

        return titularRepository.findTitularesConLicenciasVigentes(
                nombreApellido != null ? "%" + nombreApellido.trim().toUpperCase() + "%" : null,
                grupoSanguineo,
                factorRh,
                soloDonantes
        );
    }


}
