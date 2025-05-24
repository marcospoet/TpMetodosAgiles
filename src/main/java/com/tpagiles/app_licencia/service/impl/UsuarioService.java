package com.tpagiles.app_licencia.service.impl;

import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        return repo.findByMail(mail)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con mail: " + mail));
    }

    /**
     * Registra un nuevo usuario: cifra la contraseña y lo guarda.
     */
    public Usuario registrar(Usuario u) {
        u.setPassword(encoder.encode(u.getPassword()));
        return repo.save(u);
    }
}
