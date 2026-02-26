package com.tp.TripApp.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // ✅ Nom standardisé

        return new OpenAPI()
                .info(new Info()
                        .title("TripApp - Gestion des transports urbains") // ✅ Correction du titre
                        .version("1.0.0")
                        .description("API REST pour l'application de transport urbain (Zém & Taxi) au Togo. " +
                                     "Gestion des conducteurs, passagers, courses et localisations en temps réel.")
                        .contact(new Contact()
                                .name("TripApp")
                                .email("contact@tripapp.tg")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez votre token JWT")));
    }
}
