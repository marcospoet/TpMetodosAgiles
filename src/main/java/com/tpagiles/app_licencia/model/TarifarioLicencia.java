package com.tpagiles.app_licencia.model;

import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TarifarioLicencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    ClaseLicencia claseLicencia;
    @NotNull
    private int vigenciaAnios;
    @NotNull
    private double costo;

}
