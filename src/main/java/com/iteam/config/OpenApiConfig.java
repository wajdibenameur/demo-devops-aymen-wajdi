package com.iteam.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "E-Commerce API",
                version = "1.0",
                description = "API de gestion des utilisateurs, produits et commandes",
                contact = @Contact(
                        name = "Aymen Bouraoui & Wajdi Ben Ameur",
                        email = "aymen&wajdi@email.com"
                )
        )
)
public class OpenApiConfig {
}

