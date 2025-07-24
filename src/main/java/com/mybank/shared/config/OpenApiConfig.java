package com.mybank.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myBankOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080/api/v1");
        devServer.setDescription("Servidor de desarrollo");

        Info info = new Info()
                .title("MyBank API")
                .version("1.0")
                .description("API REST para gestión bancaria con autenticación JWT");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
} 