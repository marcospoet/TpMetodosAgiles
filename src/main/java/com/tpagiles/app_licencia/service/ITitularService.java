package com.tpagiles.app_licencia.service;

import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.model.Titular;

import java.util.List;
import java.util.Optional;

public interface ITitularService {
    Titular createTitular(TitularRecord titular);
    Optional<Titular> obtenerPorId(Long id);
    List<Titular> listarTodos();
    Titular actualizarTitular(Long id, TitularRecord titular);
    void eliminarTitular(Long id);
}
