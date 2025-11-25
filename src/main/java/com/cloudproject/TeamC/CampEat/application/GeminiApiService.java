package com.cloudproject.TeamC.CampEat.application;


import com.cloudproject.TeamC.CampEat.dto.response.GeminiEmbedResponse;
import com.cloudproject.TeamC.CampEat.dto.response.GeminiTextResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class GeminiApiService {

    private final WebClient webClient;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // 최신 안정 버전 모델 사용
    private final String geminiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private final String geminiEmbedEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/text-embedding-004:embedContent";

    public GeminiApiService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public String getKeywordsFromGemini(String prompt) {
        log.info("🔍 Gemini API 호출 시작");
        log.info("📍 URL: {}", geminiEndpoint);

        Map<String, Object> payload = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        log.info("📦 Payload: {}", payload);

        try {
            GeminiTextResponse response = webClient.post()
                    .uri(geminiEndpoint)
                    .header("x-goog-api-key", geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                log.error("❌ HTTP 에러 발생: {}", clientResponse.statusCode());
                                return clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.error("❌ 에러 응답 본문: {}", errorBody);
                                            return Mono.error(new RuntimeException("Gemini API 에러: " + errorBody));
                                        });
                            }
                    )
                    .bodyToMono(GeminiTextResponse.class)
                    .block();

            log.info("✅ Gemini API 호출 성공");
            return response.getCandidates().get(0).getContent().getParts().get(0).getText();

        } catch (Exception e) {
            log.error("💥 Gemini API 호출 중 예외 발생", e);
            throw e;
        }
    }

    public float[] getEmbeddingFromGemini(String text) {
        log.info("🔍 Gemini Embedding API 호출 시작");
        log.info("📍 URL: {}", geminiEmbedEndpoint);

        Map<String, Object> content = Map.of("parts", List.of(Map.of("text", text)));
        Map<String, Object> payload = Map.of(
                "model", "models/text-embedding-004",
                "content", content
        );

        log.info("📦 Embedding Payload: {}", payload);

        try {
            GeminiEmbedResponse response = webClient.post()
                    .uri(geminiEmbedEndpoint)
                    .header("x-goog-api-key", geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                log.error("❌ HTTP 에러 발생: {}", clientResponse.statusCode());
                                return clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.error("❌ 에러 응답 본문: {}", errorBody);
                                            return Mono.error(new RuntimeException("Gemini Embedding API 에러: " + errorBody));
                                        });
                            }
                    )
                    .bodyToMono(GeminiEmbedResponse.class)
                    .block();

            log.info("✅ Gemini Embedding API 호출 성공");
            return response.getEmbedding().getValues();

        } catch (Exception e) {
            log.error("💥 Gemini Embedding API 호출 중 예외 발생", e);
            throw e;
        }
    }
}
