package com.tpagiles.app_licencia.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoDocumento {
    DNI, CEDULA_IDENTIDAD, PASAPORTE, OTRO;

    @JsonCreator
    public static TipoDocumento from(String value) {
        return TipoDocumento.valueOf(value.trim().toUpperCase());
    }
}
