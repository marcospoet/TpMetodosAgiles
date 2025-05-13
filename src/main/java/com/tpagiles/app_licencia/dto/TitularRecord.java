package com.tpagiles.app_licencia.dto;

import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;

import java.time.LocalDate;

public record TitularRecord(String nombre,
                            String apellido,
                            LocalDate fechaNacimiento,
                            TipoDocumento tipoDocumento,
                            String numeroDocumento,
                            GrupoSanguineo grupoSanguineo,
                            FactorRh factorRh,
                            String direccion,
                            Boolean donanteOrganos
) {
    public Titular toTitular() {
        Titular t = new Titular();
        t.setNombre(this.nombre);
        t.setApellido(this.apellido);
        t.setFechaNacimiento(this.fechaNacimiento);
        t.setTipoDocumento(this.tipoDocumento);
        t.setNumeroDocumento(this.numeroDocumento);
        t.setGrupoSanguineo(this.grupoSanguineo);
        t.setFactorRh(this.factorRh);
        t.setDireccion(this.direccion);
        t.setDonanteOrganos(this.donanteOrganos);
        return t;
    }
}
