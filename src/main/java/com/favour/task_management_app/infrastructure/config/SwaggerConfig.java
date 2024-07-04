package com.favour.task_management_app.infrastructure.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Task Management Application",
                description = "APIs for Task Management System",
                version = "1.0",
                contact = @Contact(
                        name = "Favour Ameh",
                        email = "amehfavour77@gmail.com",
                        url = "https://github.com/thefavourameh"
                ),
                license = @License(
                        name = "Task Management Application",
                        url = "https://github.com/thefavourameh"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "RESTful API Task Management System Documentation",
                url = "https://github.com/thefavourameh"
        ),
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Auth Description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

}
