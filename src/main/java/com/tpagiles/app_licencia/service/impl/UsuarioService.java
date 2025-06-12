package com.tpagiles.app_licencia.service.impl;

import com.tpagiles.app_licencia.dto.UsuarioRecord;
import com.tpagiles.app_licencia.dto.UsuarioResponseRecord;
import com.tpagiles.app_licencia.dto.UsuarioUpdateRecord;
import com.tpagiles.app_licencia.exception.ResourceAlreadyExistsException;
import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import com.tpagiles.app_licencia.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService, IUsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

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

    @Override
    @Transactional
    public UsuarioResponseRecord crearUsuario(UsuarioRecord record) {
        if (repo.existsByMail(record.mail())) {
            throw new ResourceAlreadyExistsException("Ya existe un usuario con el email: " + record.mail());
        }

        Usuario usuario = record.toUsuario();
        usuario.setPassword(encoder.encode(record.password()));

        Usuario guardado = repo.save(usuario);
        return UsuarioResponseRecord.fromUsuario(guardado);
    }


    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseRecord> listarTodos() {
        return repo.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.name().equals("OPERADOR")))
                .map(UsuarioResponseRecord::fromUsuario)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public UsuarioResponseRecord actualizarUsuario(Long id, UsuarioUpdateRecord updated) {
        Usuario existente = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (!existente.getMail().equals(updated.mail()) &&
                repo.existsByMail(updated.mail())) {
            throw new ResourceAlreadyExistsException("Ya existe otro usuario con el email: " + updated.mail());
        }

        existente.setNombre(updated.nombre());
        existente.setApellido(updated.apellido());
        existente.setMail(updated.mail());
        existente.setRoles(updated.roles());

        // Solo actualizo contraseña si se mandó
        if (updated.password() != null && !updated.password().isBlank()) {
            existente.setPassword(encoder.encode(updated.password()));
        }

        Usuario guardado = repo.save(existente);
        return UsuarioResponseRecord.fromUsuario(guardado);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        repo.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseRecord obtenerUsuarioPorId(Long id) {
        Usuario usuario = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return UsuarioResponseRecord.fromUsuario(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseRecord activarUsuario(Long id) {
        Usuario usuario = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(true);
        Usuario actualizado = repo.save(usuario);
        return UsuarioResponseRecord.fromUsuario(actualizado);
    }
}