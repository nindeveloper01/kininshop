package com.phoneshop;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        servers = @Server(
                description ="Local ENV",
                url = "http://localhost:8080/"),
        info = @Info(title = "OpenAPI Specification Phone shop System API",
                version = "1.0",
                description = "OpenAPI Documentation for Spring Security",
                termsOfService = "Terms of Service"

        )
)

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
public class PhoneShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneShopApplication.class, args);
    }
}
