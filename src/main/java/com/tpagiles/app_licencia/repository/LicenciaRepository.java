package com.tpagiles.app_licencia.repository;

import com.tpagiles.app_licencia.model.Licencia;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LicenciaRepository extends JpaRepository<Licencia, Long> {
    long count();
    @Modifying
    @Query("UPDATE Licencia l SET l.vigente = false WHERE l.vigente = true AND l.fechaVencimiento < :fecha")
    void deactivateExpired(@Param("fecha") LocalDate fecha);
    List<Licencia> findByFechaVencimientoBefore(LocalDate fechaVencimiento);
    long countByFechaVencimientoBefore(LocalDate fechaVencimiento);
    boolean existsByTitularIdAndClaseAndVigenteTrueAndFechaVencimientoAfter(long TitularId, ClaseLicencia clase, LocalDate fechaVencimiento);
    boolean existsByTitularAndClase(Titular titular, ClaseLicencia claseLicencia);
    List<Licencia> findByTitularAndClase(Titular titular, ClaseLicencia clase);
    List<Licencia> findByTitularTipoDocumentoAndTitularNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
}