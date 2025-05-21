package com.tpagiles.app_licencia.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Licencias de Conducir")
                        .version("v1.0")
                        .description("Gestión de titulares y licencias del municipio")
                );
    }

    @Bean
    public GroupedOpenApi titularesGroup() {
        return GroupedOpenApi.builder()
                .group("titulares")
                .pathsToMatch("/api/titulares/**")
                .build();
    }

    @Bean
    public GroupedOpenApi licenciasGroup() {
        return GroupedOpenApi.builder()
                .group("licencias")
                .pathsToMatch("/api/licencias/**")
                .build();
    }

    // (Opcional) un grupo “todo” que combine ambos:
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .build();
    }
}

