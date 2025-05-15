package com.tpagiles.app_licencia.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ClaseLicencia {
    A, B, C, D, E, F, G;

    @JsonCreator
    public static ClaseLicencia from(String value) {
        return ClaseLicencia.valueOf(value.trim().toUpperCase());
    }
}