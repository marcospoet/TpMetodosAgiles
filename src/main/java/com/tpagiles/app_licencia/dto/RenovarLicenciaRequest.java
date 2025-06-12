package com.tpagiles.app_licencia.dto;

import com.tpagiles.app_licencia.model.enums.MotivoRenovacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RenovarLicenciaRequest(
        @NotNull(message = "El ID de la licencia es obligatorio")
        Long licenciaId,

        @NotNull(message = "El motivo de renovación es obligatorio")
        MotivoRenovacion motivoRenovacion,

        // Campos opcionales para cambio de datos
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nuevoNombre,

        @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
        String nuevoApellido,

        @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
        String nuevaDireccion,

        @Positive(message = "El número de copia debe ser positivo")
        Integer numeroCopia,

        @Size(max = 200, message = "El motivo de copia no puede exceder 200 caracteres")
        String motivoCopia,

        @Positive(message = "El ID de la licencia original debe ser positivo")
        Long licenciaOriginalId
) {}