package com.cloudproject.TeamC.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CloudProject Team C API 명세서",
                description = "CloudProject Team C API 명세서",
                version = "v1"
        ),
        servers = @Server(
                url = "/",
                description = "Default Server URL"
        )
)
public class SwaggerConfig {
}
