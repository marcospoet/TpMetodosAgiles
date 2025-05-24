package com.tpagiles.app_licencia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpagiles.app_licencia.dto.ErrorResponse;
import com.tpagiles.app_licencia.model.enums.Rol;
import com.tpagiles.app_licencia.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final ErrorResponseFactory factory;
    private final ObjectMapper mapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 0) Manejo de errores REST (401 y 403)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint())
                        .accessDeniedHandler(restAccessDeniedHandler())
                )
                // 1) CORS para Swagger UI
                .cors(Customizer.withDefaults())
                // 2) CSRF off (stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // 3) Sessions stateless
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4) Rutas públicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/docs/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/licencias/**")
                        .hasAnyRole(Rol.OPERADOR.name(), Rol.SUPER_USER.name())
                        .requestMatchers(HttpMethod.POST,  "/api/licencias/**")
                        .hasAnyRole(Rol.OPERADOR.name(), Rol.SUPER_USER.name())
                        .requestMatchers(HttpMethod.POST, "/api/titulares/**")
                        .hasAnyRole(Rol.OPERADOR.name(), Rol.SUPER_USER.name())
                        .requestMatchers(HttpMethod.GET, "/api/titulares/**")
                        .hasAnyRole(Rol.OPERADOR.name(), Rol.SUPER_USER.name())
                        .anyRequest().hasRole(Rol.SUPER_USER.name())
                )
                // 5) Filtro JWT
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 401 – no autenticado o token inválido
    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (req, res, ex) -> {
            ErrorResponse err = factory.build(HttpStatus.UNAUTHORIZED, "Token invalido o no proveido");
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(res.getWriter(), err);
        };
    }

    @Bean
    public AccessDeniedHandler restAccessDeniedHandler() {
        return (req, res, ex) -> {
            ErrorResponse err = factory.build(HttpStatus.FORBIDDEN, "No tienes permisos suficientes");
            res.setStatus(HttpStatus.FORBIDDEN.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(res.getWriter(), err);
        };
    }

    @Bean
    static public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization","Link","X-Total-Count"));
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);
        return src;
    }
}
