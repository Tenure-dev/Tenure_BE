package com.tenure.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tenure API")
                        .description("""
                                Tenure OOTD 기반 패션 거래 서비스 API 명세입니다.

                                운영 인증 기준은 Authorization 헤더의 Bearer JWT입니다.
                                예: Authorization: Bearer {accessToken}

                                local/dev 환경에서는 JWT 전환 전 Swagger 테스트를 위해 X-USER-ID 헤더 fallback을 임시 허용합니다.
                                prod 환경에서는 X-USER-ID fallback을 허용하지 않으며, 보호 API는 Bearer JWT 인증이 필요합니다.
                                """)
                        .version("v1"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
