// com/tpagiles/app_licencia/controllers/AuthController.java
package com.tpagiles.app_licencia.controllers;

import com.tpagiles.app_licencia.api.AuthApi;
import com.tpagiles.app_licencia.dto.AuthRequest;
import com.tpagiles.app_licencia.dto.AuthResponse;
import com.tpagiles.app_licencia.dto.RegisterRequest;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.model.enums.Rol;
import com.tpagiles.app_licencia.service.JwtService;
import com.tpagiles.app_licencia.service.impl.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthenticationManager authManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.mail(), req.password())
        );

        String token = jwtService.generateToken(
                auth.getName(),
                Map.of("roles", auth.getAuthorities().stream()
                        .map(Object::toString).toList())
        );

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Override
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        Usuario u = new Usuario();
        u.setNombre(req.nombre());
        u.setApellido(req.apellido());
        u.setMail(req.mail());
        u.setPassword(req.password());
        u.getRoles().add(Rol.OPERADOR);
        Usuario saved = usuarioService.registrar(u);

        String token = jwtService.generateToken(
                saved.getMail(),
                Map.of("roles", saved.getRoles().stream().map(Enum::name).toList())
        );

        return ResponseEntity.status(201).body(new AuthResponse(token));
    }
}
