package com.tpagiles.app_licencia.repository;

import com.tpagiles.app_licencia.dto.TitularLicenciaVigenteResponseRecord;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TitularRepository extends JpaRepository<Titular, Long> {
    boolean existsByNumeroDocumento(String numeroDocumento);
    Optional<Titular> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
    @Query("""
    SELECT new com.tpagiles.app_licencia.dto.TitularLicenciaVigenteResponseRecord(
        t.nombre,
        t.apellido,
        t.tipoDocumento,
        t.numeroDocumento,
        t.grupoSanguineo,
        t.factorRh,
        t.donanteOrganos,
        l.clase,
        l.fechaVencimiento
    )
    FROM Titular t
    JOIN t.licencias l
    WHERE l.vigente = true
    AND t.tipoDocumento IN (com.tpagiles.app_licencia.model.enums.TipoDocumento.DNI, com.tpagiles.app_licencia.model.enums.TipoDocumento.PASAPORTE)
    AND (:nombreApellido IS NULL OR 
         UPPER(t.nombre) LIKE :nombreApellido OR 
         UPPER(t.apellido) LIKE :nombreApellido)
    AND (:grupoSanguineo IS NULL OR t.grupoSanguineo IN (:grupoSanguineo))
    AND (:factorRh IS NULL OR t.factorRh = :factorRh)
    AND (:soloDonantes IS NULL OR t.donanteOrganos = :soloDonantes)
    ORDER BY t.apellido ASC, t.nombre ASC
""")
    List<TitularLicenciaVigenteResponseRecord> findTitularesConLicenciasVigentes(
            @Param("nombreApellido") String nombreApellido,
            @Param("grupoSanguineo") List<GrupoSanguineo> grupoSanguineo,
            @Param("factorRh") FactorRh factorRh,
            @Param("soloDonantes") Boolean soloDonantes
    );

}