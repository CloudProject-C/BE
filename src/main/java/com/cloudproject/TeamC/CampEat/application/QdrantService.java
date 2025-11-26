package com.cloudproject.TeamC.CampEat.application;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QdrantService {

    private final WebClient webClient;
    private final String qdrantUrl = "http://localhost:6333/collections/restaurants/points";

    private final String qdrantBaseUrl = "http://localhost:6333";

    public QdrantService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public void createCollectionIfNotExists(String collectionName, int vectorSize) {
        try {
            webClient.get()
                    .uri(qdrantBaseUrl + "/collections/" + collectionName)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("✅ Qdrant 컬렉션 '{}' 이미 존재", collectionName);

        } catch (Exception e) {
            log.info("🔨 Qdrant 컬렉션 '{}' 생성 중...", collectionName);

            Map<String, Object> payload = Map.of(
                    "vectors", Map.of(
                            "size", vectorSize,
                            "distance", "Cosine"
                    )
            );

            try {
                webClient.put()
                        .uri(qdrantBaseUrl + "/collections/" + collectionName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                log.info("✅ Qdrant 컬렉션 '{}' 생성 완료!", collectionName);

            } catch (Exception ex) {
                log.error("❌ Qdrant 컬렉션 생성 실패 - {}", collectionName, ex);
                throw new RuntimeException("Qdrant 컬렉션 생성 실패: " + collectionName, ex);
            }
        }
    }

    public void saveEmbeddingToQdrant(Long placeId, float[] embedding, String keywords) {
        StringBuilder vectorJson = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            vectorJson.append(embedding[i]);
            if (i != embedding.length - 1) vectorJson.append(",");
        }
        vectorJson.append("]");

        String payload = String.format(
                "{ \"points\": [ { \"id\": %d, \"vector\": %s, \"payload\": { \"keywords\": \"%s\" } } ] }",
                placeId, vectorJson.toString(), keywords.replace("\"", "\\\"")
        );

        webClient.put()
                .uri(qdrantUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
