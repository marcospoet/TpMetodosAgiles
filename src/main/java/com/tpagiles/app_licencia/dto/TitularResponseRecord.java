package com.tpagiles.app_licencia.dto;

import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;

import java.time.LocalDate;

public record TitularResponseRecord(
        Long id,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        GrupoSanguineo grupoSanguineo,
        FactorRh factorRh,
        String direccion,
        Boolean donanteOrganos
) {
    public static TitularResponseRecord fromEntity(Titular t) {
        return new TitularResponseRecord(
                t.getId(),
                t.getNombre(),
                t.getApellido(),
                t.getFechaNacimiento(),
                t.getTipoDocumento(),
                t.getNumeroDocumento(),
                t.getGrupoSanguineo(),
                t.getFactorRh(),
                t.getDireccion(),
                t.isDonanteOrganos()
        );
    }
}
