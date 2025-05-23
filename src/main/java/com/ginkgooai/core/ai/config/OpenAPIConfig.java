package com.ginkgooai.core.ai.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author: david
 * @date: 14:33 2025/2/20
 */

@Configuration
public class OpenAPIConfig {

    @Value("${AUTH_CLIENT}")
    private String gatewayUri;

    private static final String COOKIE_AUTH_NAME = "cookieAuth";
    private static final String WORKSPACE_HEADER = "x-workspace-id";

    /**
     * Configures and customizes the OpenAPI documentation
     * 
     * @return Configured OpenAPI instance with security and server information
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url(gatewayUri + "/api/ai")))
                .info(new Info().title("Ai Service API").version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(COOKIE_AUTH_NAME).addList(WORKSPACE_HEADER))
                .components(new Components()
                        .addSecuritySchemes(COOKIE_AUTH_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("SESSION")
                                .description("Session cookie for authentication"))
                        .addSecuritySchemes(WORKSPACE_HEADER, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(WORKSPACE_HEADER)
                                .description("Workspace ID for multi-tenant requests")));
    }
}
