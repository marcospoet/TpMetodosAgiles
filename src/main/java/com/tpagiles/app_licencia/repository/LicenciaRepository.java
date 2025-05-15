package com.tpagiles.app_licencia.repository;

import com.tpagiles.app_licencia.model.Licencia;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenciaRepository extends JpaRepository<Licencia, Long> {
    List<Licencia> findByTitularId(Long titularId);
    List<Licencia> findByVigente(boolean vigente);
    boolean findByTitularIdAndClase(long TitularId, ClaseLicencia clase);
    boolean existsByTitularAndClase(Titular titular, ClaseLicencia claseLicencia);
}