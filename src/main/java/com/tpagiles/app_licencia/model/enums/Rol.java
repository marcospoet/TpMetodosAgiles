package com.tpagiles.app_licencia.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Rol {
    OPERADOR, SUPER_USER;

    @JsonCreator
    public static Rol from(String value) {
        return Rol.valueOf(value.trim().toUpperCase());
    }
}
