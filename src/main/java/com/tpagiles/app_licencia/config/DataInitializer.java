package com.tpagiles.app_licencia.config;

import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.model.enums.Rol;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository repo;

    @Bean
    public ApplicationRunner init(PasswordEncoder encoder) {
        return args -> {
            if (!repo.existsByMail("admin@municipio.gob")) {
                repo.save(new Usuario(
                        null,
                        "Administrador",
                        "Sistema",
                        "admin@municipio.gob",
                        encoder.encode("admin123"),
                        Set.of(Rol.SUPER_USER)
                ));
            }
            if (!repo.existsByMail("operador@municipio.gob")) {
                repo.save(new Usuario(
                        null,
                        "Operador",
                        "Turno1",
                        "operador@municipio.gob",
                        encoder.encode("operador"),
                        Set.of(Rol.OPERADOR)
                ));
            }
        };
    }
}

