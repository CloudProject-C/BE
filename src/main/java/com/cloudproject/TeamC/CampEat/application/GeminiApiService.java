package com.cloudproject.TeamC.CampEat.application;


import com.cloudproject.TeamC.CampEat.dto.response.GeminiTextResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class GeminiApiService {

    private final WebClient webClient;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final String geminiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private final String geminiEmbedEndpoint =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent";

    private static final String EMBEDDING_MODEL_NAME = "models/gemini-embedding-001";

    public GeminiApiService(WebClient.Builder builder) {
        this.webClient = builder
                .defaultHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                .build();
    }

    // --- getKeywordsFromGemini 메서드는 변경 없음 ---
    public String getKeywordsFromGemini(String prompt) {

        Map<String, Object> payload = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

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

            return response.getCandidates().get(0).getContent().getParts().get(0).getText();

        } catch (Exception e) {
            throw e;
        }
    }
    public float[] getEmbeddingFromGemini(String text) {

        Map<String, Object> content = Map.of("parts", List.of(Map.of("text", text)));

        Map<String, Object> payload = Map.of(
                "model", EMBEDDING_MODEL_NAME,
                "content", content
        );

        try {
            String rawResponse = webClient.post()
                    .uri(geminiEmbedEndpoint) // :embedContent 엔드포인트
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
                                            return Mono.error(new RuntimeException("Gemini Embedding API 에러: " + errorBody));
                                        });
                            }
                    )
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawResponse);

            // 단일 요청 응답 구조에 맞게 파싱: root -> "embedding" -> "values"
            JsonNode valuesNode = root.get("embedding").get("values");

            if (valuesNode == null) {
                throw new RuntimeException("임베딩 API 응답 구조 오류");
            }

            float[] values = new float[valuesNode.size()];
            for (int i = 0; i < valuesNode.size(); i++) {
                values[i] = (float) valuesNode.get(i).asDouble();
            }

            log.info("✅ embedding[0..4]={}, {}, {}, {}, {}",
                    values[0], values[1], values[2], values[3], values[4]);

            return values;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}