package com.book.books.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "OpenAPI Specification - BookBees",
                version = "1.0",
                description = "OpenAPI documentation for Spring Security",
                contact = @Contact(
                        name = "Priyaraj",
                        email = "contact@priyaraj.coding.com",
                        url = "https://codinghyy.com/course"
                ),
                license = @License(
                        name = "License Name",
                        url = "https://some-url.com"
                ),
                termsOfService = "https://some-url.com/terms"
        ),
        servers = {
                @Server(
                        description = "Local Environment",
                        url = "http://localhost:8080/api/v1"
                ),
                @Server(
                        description = "Production Environment",
                        url = "https://learncoding.com/course"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.OAUTH2,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // No additional configuration needed here.
}