package com.tpagiles.app_licencia.repository;

import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitularRepository extends JpaRepository<Titular, Long> {
    boolean existsByNumeroDocumento(String numeroDocumento);
    Optional<Titular> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
}