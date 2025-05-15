package com.tpagiles.app_licencia.service.impl;

import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.exception.ResourceAlreadyExistsException;
import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.Titular;
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

    public Titular actualizarTitular(Long id, TitularRecord updated) {
        Titular existente = titularRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado con id: " + id));

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

}
