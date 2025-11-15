package practice.deploy.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(
                        new Info()
                                .title("CoffeePrincess REST API")
                                .description("CoffeePrincess Backend Team")
                                .contact(
                                        new Contact().name("CoffeePrincess BE Github").url("https://github.com/2025-UniD-Hackathon-Team1/BE.git"))
                                .version("1.0.0"));
    }
}
