package com.banking.banking_api.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// This config adds the "Authorize" button to Swagger UI.
// Click it, paste your JWT token, and all secured endpoints
// will automatically send Authorization: Bearer <token>
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Banking API Gateway",
        version = "1.0",
        description = "Simulated banking REST API with JWT authentication"
))
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
