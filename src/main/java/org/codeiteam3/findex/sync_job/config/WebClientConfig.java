package org.codeiteam3.findex.sync_job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://apis.data.go.kr/1160100/service/GetMarketIndexInfoService/getStockMarketIndex")
                .build();
    }
}

