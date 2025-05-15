package com.tpagiles.app_licencia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL) //basicamente filtra los campos que sean nulos asi no me llega eso
@JsonPropertyOrder({
        "nombre",
        "apellido",
        "fechaNacimiento",
        "tipoDocumento",
        "numeroDocumento",
        "grupoSanguineo",
        "factorRh",
        "direccion",
        "donanteOrganos"
})
@Schema(description = "Datos necesarios para dar de alta o modificar un Titular")
public record TitularRecord(

        @Schema(
                description = "Nombre del titular",
                example = "Juan",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
        String nombre,

        @Schema(
                description = "Apellido del titular",
                example = "Pérez",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El apellido no puede estar vacío")
        @Size(max = 50, message = "El apellido no puede superar 50 caracteres")
        String apellido,

        @Schema(
                description = "Fecha de nacimiento del titular",
                example = "2000-04-30",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser anterior al día de hoy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaNacimiento,

        @Schema(
                description = "Tipo de documento",
                example = "DNI",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDocumento tipoDocumento,

        @Schema(
                description = "Número de documento",
                example = "12345678",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El número de documento no puede estar vacío")
        @Size(max = 20, message = "El número de documento no puede superar 20 caracteres")
        String numeroDocumento,

        @Schema(
                description = "Grupo sanguíneo",
                example = "AB",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "El grupo sanguíneo es obligatorio")
        GrupoSanguineo grupoSanguineo,

        @Schema(
                description = "Factor RH",
                example = "NEGATIVO",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "El factor RH es obligatorio")
        FactorRh factorRh,

        @Schema(
                description = "Domicilio del titular",
                example = "San Martín 1234, Ciudad",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "La dirección no puede estar vacía")
        @Size(max = 150, message = "La dirección no puede superar 150 caracteres")
        String direccion,

        @Schema(
                description = "Indica si el titular es donante de órganos",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "El campo donanteOrgános es obligatorio")
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
