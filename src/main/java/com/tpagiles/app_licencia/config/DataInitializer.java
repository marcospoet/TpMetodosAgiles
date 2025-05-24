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
    private final PasswordEncoder encoder;

    @Bean
    public ApplicationRunner init() {
        return args -> {
            if (!repo.existsByMail("admin")) {
                repo.save(new Usuario(
                        null,
                        "Administrador",
                        "Sistema",
                        "admin@municipio.gob",                                // mail
                        encoder.encode("admin123"),
                        Set.of(Rol.SUPER_USER)
                ));
            }
            if (!repo.existsByMail("operador")) {
                repo.save(new Usuario(
                        null,
                        "Operador",
                        "Turno1",
                        "operador@municipio.gob",                            // mail
                        encoder.encode("operador"),
                        Set.of(Rol.OPERADOR)
                ));
            }
        };
    }
}
