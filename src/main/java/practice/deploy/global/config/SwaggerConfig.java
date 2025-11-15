package practice.deploy.global.config;

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
    public OpenAPI openAPI() {
        SecurityScheme securityScheme =
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .description("Auto Configured Server"))
                .components(new Components().addSecuritySchemes("BearerAuth", securityScheme))
                .info(new Info()
                        .title("CoffeePrincess REST API")
                        .description("CoffeePrincess Backend Team")
                        .contact(new Contact().name("CoffeePrincess BE Github").url("https://github.com/2025-UniD-Hackathon-Team1/BE.git"))
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement);
    }
}
