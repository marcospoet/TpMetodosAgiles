package com.tpagiles.app_licencia.model.enums;

public enum MotivoRenovacion {
    VENCIDA,
    CAMBIO_DATOS;

    @Override
    public String toString() {
        return name();
    }
}