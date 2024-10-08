package com.example.helloworld.utils;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.helloworld.model.PredictRequest;
import com.example.helloworld.model.PredictResponse;
import reactor.core.publisher.Mono;

@Component
public class HttpClientUtils {

    private final WebClient webClient;

    public HttpClientUtils(WebClient.Builder webClientBuilder, @Value("${ml.server.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public CompletableFuture<PredictResponse> sendMlRequest(PredictRequest mlRequest) {
        // 비동기 요청
        Mono<PredictResponse> response = webClient.post()
                .uri("/predict")
                .bodyValue(mlRequest)
                .retrieve()
                .bodyToMono(PredictResponse.class);

        return response.toFuture();
    }
}
