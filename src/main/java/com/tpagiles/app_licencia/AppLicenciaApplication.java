package com.tpagiles.app_licencia;

import com.tpagiles.app_licencia.model.TarifarioLicencia;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.model.enums.Rol;
import com.tpagiles.app_licencia.repository.TarifarioLicenciaRepository;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;

@SpringBootApplication
public class AppLicenciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppLicenciaApplication.class, args);
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner cargaUsuarios(UsuarioRepository usuarioRepo) {
		return args -> {
			if (usuarioRepo.count() == 0) {
				// Usuario ADMIN
				Usuario admin = new Usuario();
				admin.setNombre("Administrador");
				admin.setApellido("Sistema");
				admin.setFechaNacimiento(LocalDate.of(1970, 1, 1)); // fecha dummy válida
				admin.setUsername("admin");
				admin.setPassword("admin123");
				admin.setRol(Rol.SUPER_USER);
				usuarioRepo.save(admin);

				// Usuario OPERADOR
				Usuario operador = new Usuario();
				operador.setNombre("Operador");
				operador.setApellido("Turno1");
				operador.setFechaNacimiento(LocalDate.of(1985, 6, 15));
				operador.setUsername("operador");
				operador.setPassword("operador123");
				operador.setRol(Rol.OPERADOR);
				usuarioRepo.save(operador);
			}
		};
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner cargaTarifario(TarifarioLicenciaRepository repo) {
		return args -> {
			if (repo.count() == 0) {
				// Clase A
				repo.save(new TarifarioLicencia(null, ClaseLicencia.A, 5, 40.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.A, 4, 30.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.A, 3, 25.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.A, 1, 20.0));
				// Clase B
				repo.save(new TarifarioLicencia(null, ClaseLicencia.B, 5, 40.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.B, 4, 30.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.B, 3, 25.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.B, 1, 20.0));
				// Clase C
				repo.save(new TarifarioLicencia(null, ClaseLicencia.C, 5, 47.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.C, 4, 35.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.C, 3, 30.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.C, 1, 23.0));
				// Clase E
				repo.save(new TarifarioLicencia(null, ClaseLicencia.E, 5, 59.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.E, 4, 44.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.E, 3, 39.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.E, 1, 29.0));
				// Clase G
				repo.save(new TarifarioLicencia(null, ClaseLicencia.G, 5, 40.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.G, 4, 30.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.G, 3, 25.0));
				repo.save(new TarifarioLicencia(null, ClaseLicencia.G, 1, 20.0));
			}
		};
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:3000")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
}