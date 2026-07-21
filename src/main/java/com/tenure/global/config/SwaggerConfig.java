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

                                인증이 필요한 API는 최종적으로 Authorization 헤더에 Bearer JWT를 전달하는 방식으로 사용합니다.
                                예: Authorization: Bearer {accessToken}

                                현재 JWT 전환 전까지 로컬/개발 환경에서는 기존 X-USER-ID 헤더를 임시 인증 fallback으로 함께 사용할 수 있습니다.
                                운영 환경에서는 X-USER-ID를 사용하지 않고 Bearer JWT만 허용하는 방향으로 전환해야 합니다.
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
