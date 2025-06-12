package com.tpagiles.app_licencia.controllers;
import com.tpagiles.app_licencia.api.UsuarioApi;
import com.tpagiles.app_licencia.dto.UsuarioRecord;
import com.tpagiles.app_licencia.dto.UsuarioResponseRecord;
import com.tpagiles.app_licencia.service.IUsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsuarioController implements UsuarioApi {

    private final IUsuarioService usuarioService;

    @Override
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<UsuarioResponseRecord> crearUsuario(
            @Valid @RequestBody UsuarioRecord usuarioDTO) {
            var nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoUsuario);
    }

    @Override
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<List<UsuarioResponseRecord>> listarUsuarios() {
        var usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @Override
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<UsuarioResponseRecord> actualizarUsuario(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UsuarioRecord usuarioDTO) {
        var usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @Override
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable @Positive Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<UsuarioResponseRecord> obtenerUsuarioPorId(@PathVariable @Positive Long id) {
        var usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @Override
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<UsuarioResponseRecord> activarUsuario(@PathVariable @Positive Long id) {
        var usuarioActivado = usuarioService.activarUsuario(id);
        return ResponseEntity.ok(usuarioActivado);
    }
}