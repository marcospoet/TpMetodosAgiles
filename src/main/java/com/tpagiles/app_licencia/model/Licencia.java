package com.tpagiles.app_licencia.model;

import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "licencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Licencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "titular_id", nullable = false)
    private Titular titular;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "clase", nullable = false, length = 20)
    private ClaseLicencia clase;

    @NotNull
    @Column(name = "vigencia_anios", nullable = false)
    private int vigenciaAnios;

    @NotNull
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @NotNull
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @NotNull
    @Column(name = "costo", nullable = false)
    private double costo;

    @Column(name = "numero_copia")
    private Integer numeroCopia;

    @ManyToOne
    @JoinColumn(name = "licencia_original_id")
    private Licencia licenciaOriginal;

    @Column(name = "motivo_copia", length = 200)
    private String motivoCopia;

    @NotNull
    @Column(name = "vigente", nullable = false)
    private boolean vigente;


    @ManyToOne(fetch = FetchType.LAZY, optional = false) //para que no cargue automaticamente la relacion, sólo se materializa si llamas a licencia.getEmisor()
    @JoinColumn(name = "emisor_id", nullable = false)
    private Usuario emisor;

}
