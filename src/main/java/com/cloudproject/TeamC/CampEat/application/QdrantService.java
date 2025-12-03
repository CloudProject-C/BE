package com.cloudproject.TeamC.CampEat.application;


import com.cloudproject.TeamC.CampEat.dto.response.QdrantScrollResponse;
import com.cloudproject.TeamC.CampEat.dto.response.QdrantSearchHit;
import com.cloudproject.TeamC.CampEat.dto.response.QdrantSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QdrantService {

    private final WebClient webClient;
    private final String qdrantRestaurantsUrl = "http://localhost:6333/collections/restaurants/points";
    private final String qdrantOnboardingUrl = "http://localhost:6333/collections/onboarding/points";

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

        } catch (Exception e) {

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

//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    // QdrantService.java (savePoint 메서드)
//    // QdrantService.java
//
//    private void savePoint(
//            String url,
//            long id,
//            float[] embedding,
//            Map<String, Object> payloadFields
//    ) {
//        try {
//            // 1. Vector JSON: ObjectMapper를 사용하여 float[]를 안전한 JSON 배열로 변환
//            String vectorJson = objectMapper.writeValueAsString(embedding);
//
//            // 2. Payload JSON: ObjectMapper를 사용하여 Map<String, Object>를 안전한 JSON 객체로 변환
//            // keywords는 문자열이므로 JSON String 포맷으로 변환됩니다.
//            String payloadJson = objectMapper.writeValueAsString(payloadFields);
//
//            // 3. Final payload construction
//            String finalPayload = String.format(
//                    "{ \"points\": [ { \"id\": %d, \"vector\": %s, \"payload\": %s } ] }",
//                    id,
//                    vectorJson,
//                    payloadJson
//            );
//
//            // 4. WebClient Call with Robust Error Handling (로그 및 예외 처리 포함)
//            String response = webClient.put()
//                    .uri(url)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .bodyValue(finalPayload)
//                    .retrieve()
//                    .onStatus(
//                            status -> status.is4xxClientError() || status.is5xxServerError(),
//                            clientResponse -> clientResponse.bodyToMono(String.class)
//                                    .flatMap(errorBody -> {
//                                        log.error("❌ Qdrant HTTP 에러 발생: {} - 응답 본문: {}", clientResponse.statusCode(), errorBody);
//                                        return Mono.error(new RuntimeException("Qdrant API 저장 실패: " + errorBody));
//                                    })
//                    )
//                    .bodyToMono(String.class)
//                    .block();
//
//            // Qdrant 서버 응답을 확인하여 최종 상태가 'ok'가 아니면 예외 발생 (추가적인 안전장치)
//            JsonNode root = objectMapper.readTree(response);
//            if (!"ok".equals(root.get("status").asText())) {
//                log.error("❌ Qdrant 서버에서 상태 'ok'를 받지 못함. 전체 응답: {}", response);
//                throw new RuntimeException("Qdrant 저장 실패 - 서버 응답 오류");
//            }
//
//            log.info("✅ Qdrant Point 저장 성공. ID: {}, 응답: {}", id, response);
//
//        } catch (Exception e) {
//            // 네트워크 오류나 Jackson JSON 변환 오류, Qdrant API 에러 처리
//            log.error("💥 Qdrant Point 저장 중 예외 발생. ID: {}", id, e);
//            // 트랜잭션 롤백을 위해 예외 재전파
//            throw new RuntimeException("Qdrant 저장 실패", e);
//        }
//    }
    private void savePoint(
            String url,
            long id,
            float[] embedding,
            Map<String, Object> payloadFields
    ) {
        StringBuilder vectorJson = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            vectorJson.append(embedding[i]);
            if (i != embedding.length - 1) vectorJson.append(",");
        }
        vectorJson.append("]");

        StringBuilder payloadJson = new StringBuilder("{");
        int idx = 0;
        for (Map.Entry<String, Object> entry : payloadFields.entrySet()) {
            payloadJson.append("\"")
                    .append(entry.getKey())
                    .append("\": \"")
                    .append(String.valueOf(entry.getValue()).replace("\"", "\\\""))
                    .append("\"");
            if (idx < payloadFields.size() - 1) {
                payloadJson.append(", ");
            }
            idx++;
        }
        payloadJson.append("}");

        String payload = String.format(
                "{ \"points\": [ { \"id\": %d, \"vector\": %s, \"payload\": %s } ] }",
                id,
                vectorJson.toString(),
                payloadJson.toString()
        );

        webClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void saveEmbeddingToQdrant(Long placeId, float[] embedding, String keywords) {
        Map<String, Object> payloadFields = Map.of(
                "keywords", keywords
        );
        savePoint(qdrantRestaurantsUrl, placeId, embedding, payloadFields);
    }

    public void saveOnboardingEmbedding(Long userId, float[] embedding, List<String> features) {
        String featuresJoined = String.join(" ", features);
        Map<String, Object> payloadFields = Map.of(
                "userId", userId,          // ← 추가
                "features", featuresJoined
        );
        savePoint(qdrantOnboardingUrl, userId, embedding, payloadFields);
    }

    public float[] getOnboardingEmbedding(Long userId) {
        String url = qdrantBaseUrl + "/collections/onboarding/points/scroll";

        Map<String, Object> mustFilter = Map.of(
                "key", "userId",
                "match", Map.of("value", userId)
        );

        Map<String, Object> req = Map.of(
                "filter", Map.of("must", List.of(mustFilter)),
                "with_vector", true,
                "limit", 1
        );

        QdrantScrollResponse resp = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(QdrantScrollResponse.class)
                .block();

        if (resp == null
                || resp.getResult() == null
                || resp.getResult().getPoints() == null
                || resp.getResult().getPoints().isEmpty()) {
            return null;
        }

        List<Double> vec = resp.getResult().getPoints().get(0).getVector();
        float[] arr = new float[vec.size()];
        for (int i = 0; i < vec.size(); i++) {
            arr[i] = vec.get(i).floatValue();
        }
        return arr;
    }



    public List<QdrantSearchHit> searchTopNInRestaurants(float[] embedding, int topN) {
        String url = qdrantBaseUrl + "/collections/restaurants/points/search";

        List<Float> vectorList = new ArrayList<>(embedding.length);
        for (float v : embedding) vectorList.add(v);

        Map<String, Object> req = Map.of(
                "vector", vectorList,
                "top", topN,
                "with_payload", true
        );

        QdrantSearchResponse resp = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(QdrantSearchResponse.class)
                .block();

        List<QdrantSearchHit> hits = new ArrayList<>();
        if (resp != null && resp.getResult() != null) {
            for (QdrantSearchResponse.Result r : resp.getResult()) {
                QdrantSearchHit hit = new QdrantSearchHit();
                hit.setId(r.getId());
                hit.setScore(r.getScore()); // 그대로 0~1, 나중에 *100 해서 %로 사용 가능
                hit.setPayload(r.getPayload());
                hits.add(hit);
            }
        }
        return hits;
    }
}



//    public void saveEmbeddingToQdrant(Long placeId, float[] embedding, String keywords) {
//        StringBuilder vectorJson = new StringBuilder("[");
//        for (int i = 0; i < embedding.length; i++) {
//            vectorJson.append(embedding[i]);
//            if (i != embedding.length - 1) vectorJson.append(",");
//        }
//        vectorJson.append("]");
//
//        String payload = String.format(
//                "{ \"points\": [ { \"id\": %d, \"vector\": %s, \"payload\": { \"keywords\": \"%s\" } } ] }",
//                placeId, vectorJson.toString(), keywords.replace("\"", "\\\"")
//        );
//
//        webClient.put()
//                .uri(qdrantRestaurantsUrl)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
//
//    public void saveOnboardingEmbedding(Long userId, float[] embedding, List<String> features) {
//        StringBuilder vectorJson = new StringBuilder("[");
//        for (int i = 0; i < embedding.length; i++) {
//            vectorJson.append(embedding[i]);
//            if (i != embedding.length - 1) vectorJson.append(",");
//        }
//        vectorJson.append("]");
//
//        String featuresJoined = String.join(" ", features); // 예: 한 문장으로 저장
//
//        String payload = String.format(
//                "{ \"points\": [ { \"id\": %d, \"vector\": %s, \"payload\": { \"features\": \"%s\" } } ] }",
//                userId,
//                vectorJson.toString(),
//                featuresJoined.replace("\"", "\\\"")
//        );
//
//        webClient.put()
//                .uri(qdrantOnboardingUrl)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }

