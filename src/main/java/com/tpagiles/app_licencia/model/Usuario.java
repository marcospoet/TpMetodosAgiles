package com.tpagiles.app_licencia.model;

import com.tpagiles.app_licencia.model.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios",
        uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario extends Persona {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String username;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

}
