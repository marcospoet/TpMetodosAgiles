package com.tpagiles.app_licencia.config;

import com.tpagiles.app_licencia.model.enums.Rol;
import com.tpagiles.app_licencia.security.JwtAuthenticationFilter;
import com.tpagiles.app_licencia.service.impl.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UsuarioService usuarioService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1) Habilita CORS para que Swagger UI pueda hacer peticiones desde el navegador
                .cors(Customizer.withDefaults())
                // 2) Desactiva CSRF (API stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // 3) Stateless sessions
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4) Rutas publicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        // Exponemos Swagger y OpenAPI sin autenticación
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/docs/**"
                        ).permitAll()
                        // Autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        // Roles
                        .requestMatchers(HttpMethod.GET,  "/api/licencias/**")
                        .hasAnyRole(Rol.OPERADOR.name(), Rol.SUPER_USER.name())
                        .requestMatchers(HttpMethod.POST, "/api/titulares/**")
                        .hasAnyRole(Rol.OPERADOR.name(), Rol.SUPER_USER.name())
                        .anyRequest().hasRole(Rol.SUPER_USER.name())
                )
                // 5) Proveedor de usuario y filtro JWT
                .authenticationProvider(daoAuthProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    static public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    /**
     * Configuración global de CORS para permitir peticiones desde Swagger UI
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));                     // o tu dominio específico
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization","Link","X-Total-Count")); // si necesitas exponer cabeceras específicas

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
