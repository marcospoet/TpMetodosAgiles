package com.tpagiles.app_licencia.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FactorRh {
    POSITIVO, NEGATIVO;

    @JsonCreator
    public static FactorRh from(String value) {
        return FactorRh.valueOf(value.trim().toUpperCase());
    }
}
