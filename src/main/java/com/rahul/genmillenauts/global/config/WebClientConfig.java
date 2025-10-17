package com.rahul.genmillenauts.global.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	@Value("${huggingface.api.token}")
    private String apiToken;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
            .defaultHeader("Authorization", "Bearer " + apiToken)
            .build();
    }
}
