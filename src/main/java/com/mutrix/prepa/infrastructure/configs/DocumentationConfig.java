package com.mutrix.prepa.infrastructure.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentationConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mutrix Prepa API")
                        .version("1.0")
                        .description("API pour la gestion des inscriptions, des authentifications et des utilisateurs de Mutrix Prepa"));
    }
}
