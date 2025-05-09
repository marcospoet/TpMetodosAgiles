package com.tpagiles.app_licencia.model;

import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "titulares", uniqueConstraints = {
        @UniqueConstraint(columnNames = "numero_documento")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Titular extends Persona {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @NotBlank
    @Size(max = 50)
    @Column(name = "numero_documento", nullable = false, length = 50)
    private String numeroDocumento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_sanguineo", nullable = false, length = 5)
    private GrupoSanguineo grupoSanguineo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "factor_rh", nullable = false, length = 10)
    private FactorRh factorRh;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(name = "donante_organos", nullable = false)
    private boolean donanteOrganos;

    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default //asigno el valor incial
    private List<Licencia> licencias = new ArrayList<>();

}
