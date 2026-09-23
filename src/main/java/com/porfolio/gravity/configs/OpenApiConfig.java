package com.porfolio.gravity.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gravityOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gravity API")
                        .description("REST API for Gravity application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TrinhNH")));
    }
}
