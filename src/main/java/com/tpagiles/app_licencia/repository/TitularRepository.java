package com.tpagiles.app_licencia.repository;

import com.tpagiles.app_licencia.model.Titular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitularRepository extends JpaRepository<Titular, Long> {
    boolean existsByNumeroDocumento(String numeroDocumento);
    Titular findByNumeroDocumento(String numeroDocumento);
}