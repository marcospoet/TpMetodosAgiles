package com.tpagiles.app_licencia.repository;

import com.tpagiles.app_licencia.model.Licencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenciaRepository extends JpaRepository<Licencia, Long> {
    List<Licencia> findByTitularId(Long titularId);
    List<Licencia> findByVigente(boolean vigente);
}