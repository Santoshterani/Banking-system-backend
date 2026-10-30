package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Core Banking System API",
                version = "1.0",
                description = "Spring Boot + Spring Security + JWT + MySQL banking backend"
        )
)
@SecurityScheme(
        name = "cookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = io.swagger.v3.oas.annotations.enums.ParameterIn.COOKIE,
        paramName = "jwt"
)
public class OpenApiConfig {
}
