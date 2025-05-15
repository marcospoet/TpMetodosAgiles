package com.tpagiles.app_licencia.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GrupoSanguineo {
    A, AB, B, O;

    @JsonCreator
    public static GrupoSanguineo from(String value) {
        return GrupoSanguineo.valueOf(value.trim().toUpperCase());
    }
}
