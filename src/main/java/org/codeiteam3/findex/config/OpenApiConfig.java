package org.codeiteam3.findex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080/index.html")
                .description("local url");

        return new OpenAPI()
                .info(new Info()
                        .title("Findex API")
                        .description("가볍고 빠른 외부 API 연동 금융 분석 도구 API 문서")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                        .version("v1.0.0"))
                .servers(List.of(localServer));
    }
}
