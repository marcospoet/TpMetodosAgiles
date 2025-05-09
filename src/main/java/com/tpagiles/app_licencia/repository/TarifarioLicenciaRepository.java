package com.tpagiles.app_licencia.repository;

import com.tpagiles.app_licencia.model.TarifarioLicencia;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifarioLicenciaRepository extends JpaRepository<TarifarioLicencia, Long> {
    Optional<TarifarioLicencia> findByClaseLicenciaAndVigenciaAnios(ClaseLicencia claseLicencia, int vigenciaAnios);
}