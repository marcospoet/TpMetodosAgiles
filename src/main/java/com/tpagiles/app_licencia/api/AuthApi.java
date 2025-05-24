package com.tpagiles.app_licencia.api;

import com.tpagiles.app_licencia.dto.AuthRequest;
import com.tpagiles.app_licencia.dto.AuthResponse;
import com.tpagiles.app_licencia.dto.ErrorResponse;
import com.tpagiles.app_licencia.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Login y registro de usuarios")
@RequestMapping("/api/auth")
@Validated
public interface AuthApi {

    @Operation(
            summary     = "Iniciar sesión",
            description = "Valida las credenciales y devuelve un JWT válido para acceder al resto de la API.",
            requestBody = @RequestBody(
                    description = "Mail y contraseña del usuario",
                    required    = true,
                    content     = @Content(
                            mediaType = "application/json",
                            schema    = @Schema(implementation = AuthRequest.class),
                            examples  = @ExampleObject(
                                    name  = "Login",
                                    value = """
                    {
                      "mail": "admin@municipio.gob",
                      "password": "admin123"
                    }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description  = "JWT generado correctamente",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = AuthResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "TokenResponse",
                                            value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description  = "Credenciales inválidas",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "Unauthorized",
                                            value = """
                        {
                          "timestamp": "2025-05-23T21:00:00Z",
                          "status": 401,
                          "message": "Credenciales inválidas"
                        }"""
                                    )
                            )
                    )
            }
    )
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req);


    @Operation(
            summary     = "Registrar usuario",
            description = "Crea un nuevo usuario con rol OPERADOR y devuelve inmediatamente su JWT.",
            requestBody = @RequestBody(
                    description = "Datos necesarios para el registro",
                    required    = true,
                    content     = @Content(
                            mediaType = "application/json",
                            schema    = @Schema(implementation = RegisterRequest.class),
                            examples  = @ExampleObject(
                                    name  = "Registro",
                                    value = """
                    {
                      "nombre": "Marcos",
                      "apellido": "Poet",
                      "mail": "marcos@tp.com",
                      "password": "miPass123"
                    }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description  = "Usuario creado y JWT entregado",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = AuthResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "TokenResponse",
                                            value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }"""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description  = "Datos inválidos o mail ya registrado",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = ErrorResponse.class),
                                    examples  = @ExampleObject(
                                            name  = "BadRequest",
                                            value = """
                        {
                          "timestamp": "2025-05-23T21:05:00Z",
                          "status": 400,
                          "message": "Mail ya registrado o datos inválidos"
                        }"""
                                    )
                            )
                    )
            }
    )
    @PostMapping("/register")
    ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req);

}
