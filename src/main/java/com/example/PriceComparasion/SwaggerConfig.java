package com.example.PriceComparasion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Price Comparison API")
                        .version("1.0.0")
                        .description("API for product search, price comparison, and currency conversion.")
                        .contact(new Contact()
                                .name("Support Team")
                                .email("support@example.com")));
    }
}
